package benjaminv_isabel.app.salasmart

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Todo lo que se comparte entre teléfonos (Firebase Auth + Firestore). */
object Nube {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    private fun idAsiento(sala: String, numero: Int) =
        "${RepositorioAsientos.claveSala(sala)}_$numero"

    // ---------- Lectura en tiempo real ----------
    private fun <T> Query.vivo(mapa: (DocumentSnapshot) -> T?): Flow<List<T>> = callbackFlow {
        val reg = addSnapshotListener { snap, err ->
            if (err != null) { trySend(emptyList()); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull(mapa) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    private fun aAsiento(d: DocumentSnapshot): Asiento? {
        val sala = d.getString("sala") ?: return null
        val numero = d.getLong("numero")?.toInt() ?: return null
        return Asiento(sala, numero, d.getString("usuario") ?: "")
    }

    fun asientos(sala: String): Flow<List<Asiento>> =
        db.collection("asientos").whereEqualTo("sala", sala).vivo { aAsiento(it) }

    fun todosAsientos(): Flow<List<Asiento>> =
        db.collection("asientos").vivo { aAsiento(it) }

    fun usuarios(): Flow<List<CuentaApp>> =
        db.collection("usuarios").vivo { d -> d.getString("correo")?.let { CuentaApp(it, "") } }

    // ---------- Reservas ----------
    /** Reserva todo o nada. Devuelve los asientos que ya estaban tomados (vacío = éxito). */
    suspend fun reservar(sala: String, numeros: Set<Int>, usuario: String): Set<Int> {
        val lista = numeros.toList()
        val refs = lista.map { db.collection("asientos").document(idAsiento(sala, it)) }
        return db.runTransaction { tx ->
            val tomados = lista.filterIndexed { i, _ -> tx.get(refs[i]).exists() }.toSet()
            if (tomados.isEmpty()) {
                lista.forEachIndexed { i, n ->
                    tx.set(refs[i], mapOf("sala" to sala, "numero" to n, "usuario" to usuario))
                }
            }
            tomados
        }.await()
    }

    suspend fun liberarAsiento(sala: String, numero: Int) {
        db.collection("asientos").document(idAsiento(sala, numero)).delete().await()
    }

    suspend fun liberarSala(sala: String) {
        val docs = db.collection("asientos").whereEqualTo("sala", sala).get().await()
        val batch = db.batch()
        docs.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    // ---------- Autenticación (devuelven null si salió bien, o el mensaje de error) ----------
    suspend fun registrar(correo: String, clave: String): String? {
        try {
            auth.createUserWithEmailAndPassword(correo, clave).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            return "Ya existe una cuenta con ese correo"
        } catch (e: Exception) {
            return "No se pudo crear la cuenta. Revisa tu conexión."
        }
        try {
            db.collection("usuarios").document(correo).set(mapOf("correo" to correo)).await()
        } catch (_: Exception) { }
        return null
    }

    suspend fun iniciar(correo: String, clave: String): String? = try {
        auth.signInWithEmailAndPassword(correo, clave).await()
        null
    } catch (e: FirebaseAuthInvalidUserException) {
        "No existe una cuenta con ese correo. Crea una cuenta primero."
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        "Correo o contraseña incorrectos"
    } catch (e: Exception) {
        "No se pudo iniciar sesión. Revisa tu conexión."
    }

    fun cerrarSesion() = auth.signOut()
}
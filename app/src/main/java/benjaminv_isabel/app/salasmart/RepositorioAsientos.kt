package benjaminv_isabel.app.salasmart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object RepositorioAsientos {
    private const val BASE = "https://salasmart-d7d55-default-rtdb.firebaseio.com"

    // Firebase no acepta . $ # [ ] / en las claves
    fun claveSala(nombre: String) = nombre.replace(Regex("[^A-Za-z0-9_]"), "_")

    private fun llamar(url: String, metodo: String, cuerpo: String? = null): String {
        val con = URL(url).openConnection() as HttpURLConnection
        con.requestMethod = metodo
        con.connectTimeout = 5000
        con.readTimeout = 5000
        if (cuerpo != null) {
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.outputStream.use { it.write(cuerpo.toByteArray()) }
        }
        return try {
            con.inputStream.bufferedReader().use { it.readText() }
        } finally {
            con.disconnect()
        }
    }

    /** Devuelve los asientos reservados, o null si no hay conexión. */
    suspend fun leer(sala: String): Set<Int>? = withContext(Dispatchers.IO) {
        try {
            val texto = llamar("$BASE/asientos/$sala.json", "GET")
            if (texto == "null" || texto.isBlank()) emptySet()
            else {
                val json = JSONObject(texto)
                json.keys().asSequence()
                    .mapNotNull { it.removePrefix("s").toIntOrNull() }
                    .toSet()
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Reserva los asientos. Devuelve los que ya estaban tomados (vacío = éxito). */
    suspend fun reservar(sala: String, asientos: Set<Int>): Set<Int> = withContext(Dispatchers.IO) {
        try {
            val actuales = leer(sala) ?: return@withContext asientos
            val ocupados = asientos.intersect(actuales)
            if (ocupados.isNotEmpty()) return@withContext ocupados
            asientos.forEach { n ->
                llamar("$BASE/asientos/$sala/s$n.json", "PUT", "\"reservado\"")
            }
            emptySet()
        } catch (e: Exception) {
            asientos
        }
    }

    /** Libera todos los asientos de la sala (útil para la demo). */
    suspend fun reiniciar(sala: String) = withContext(Dispatchers.IO) {
        try { llamar("$BASE/asientos/$sala.json", "DELETE") } catch (_: Exception) {}
    }
}
package benjaminv_isabel.app.salasmart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.security.MessageDigest

@Entity(tableName = "asientos", primaryKeys = ["sala", "numero"])
data class Asiento(
    val sala: String,
    val numero: Int,
    val usuario: String = ""   // correo de quien reservó
)

// Se llama CuentaApp para no chocar con otras clases "Usuario" del proyecto
@Entity(tableName = "cuentas")
data class CuentaApp(
    @PrimaryKey val correo: String,
    val claveHash: String
)

@Dao
interface AsientoDao {
    @Query("SELECT numero FROM asientos WHERE sala = :sala")
    fun observar(sala: String): LiveData<List<Int>>

    @Query("SELECT * FROM asientos")
    fun observarTodos(): LiveData<List<Asiento>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(lista: List<Asiento>)

    @Query("DELETE FROM asientos WHERE sala = :sala")
    suspend fun liberar(sala: String)

    @Query("DELETE FROM asientos WHERE sala = :sala AND numero = :numero")
    suspend fun liberarAsiento(sala: String, numero: Int)
}

@Dao
interface CuentaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(cuenta: CuentaApp)

    @Query("SELECT * FROM cuentas WHERE correo = :correo LIMIT 1")
    suspend fun buscar(correo: String): CuentaApp?

    @Query("SELECT * FROM cuentas")
    fun observarTodos(): LiveData<List<CuentaApp>>
}

// Base de datos propia (no toca SmartSalaBD)
@Database(entities = [Asiento::class, CuentaApp::class], version = 2, exportSchema = false)
abstract class AsientosBD : RoomDatabase() {

    abstract fun asientoDao(): AsientoDao
    abstract fun usuarioDao(): CuentaDao

    companion object {
        @Volatile
        private var INIT: AsientosBD? = null

        fun obtener(contexto: Context): AsientosBD {
            return INIT ?: synchronized(this) {
                val bd = Room.databaseBuilder(
                    contexto.applicationContext,
                    AsientosBD::class.java,
                    "asientos_bd"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INIT = bd
                bd
            }
        }
    }
}

/** Registro e inicio de sesión. Devuelve null si todo salió bien, o el mensaje de error. */
object Cuentas {
    private const val CORREO_ADMIN = "admin@salasmart.cl"

    private fun cifrar(clave: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(clave.toByteArray())
            .joinToString("") { "%02x".format(it) }

    suspend fun registrar(dao: CuentaDao, correo: String, clave: String): String? {
        if (correo == CORREO_ADMIN) return "Ese correo está reservado"
        if (dao.buscar(correo) != null) return "Ya existe una cuenta con ese correo"
        dao.insertar(CuentaApp(correo, cifrar(clave)))
        return null
    }

    suspend fun iniciar(dao: CuentaDao, correo: String, clave: String): String? {
        val cuenta = dao.buscar(correo)
            ?: return "No existe una cuenta con ese correo. Crea una cuenta primero."
        return if (cuenta.claveHash == cifrar(clave)) null else "Contraseña incorrecta"
    }
}
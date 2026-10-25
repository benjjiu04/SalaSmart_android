package benjaminv_isabel.app.salasmart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "asientos", primaryKeys = ["sala", "numero"])
data class Asiento(
    val sala: String,
    val numero: Int
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
}

// Base de datos propia para los asientos (no toca SmartSalaBD)
@Database(entities = [Asiento::class], version = 1, exportSchema = false)
abstract class AsientosBD : RoomDatabase() {

    abstract fun asientoDao(): AsientoDao

    companion object {
        @Volatile
        private var INIT: AsientosBD? = null

        fun obtener(contexto: Context): AsientosBD {
            return INIT ?: synchronized(this) {
                val bd = Room.databaseBuilder(
                    contexto.applicationContext,
                    AsientosBD::class.java,
                    "asientos_bd"
                ).build()
                INIT = bd
                bd
            }
        }
    }
}
package benjaminv_isabel.app.salasmart

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val claveHash: String
)

// El índice único en salaId impide reservar dos veces la misma sala
@Entity(tableName = "reservas", indices = [Index(value = ["salaId"], unique = true)])
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val salaId: Long,
    val usuarioEmail: String
)

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas")
    fun obtenerReservas(): Flow<List<Reserva>>

    // Devuelve -1 si la sala ya estaba reservada
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun reservar(reserva: Reserva): Long

    @Query("DELETE FROM reservas WHERE salaId = :salaId AND usuarioEmail = :email")
    suspend fun cancelar(salaId: Long, email: String)

    @Query("SELECT COUNT(*) FROM salas")
    suspend fun contarSalas(): Int

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarUsuario(email: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registrar(usuario: Usuario): Long
}
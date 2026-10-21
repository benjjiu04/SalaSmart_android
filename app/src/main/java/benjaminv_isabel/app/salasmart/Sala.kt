package benjaminv_isabel.app.salasmart
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

// crea la tabla "salas"
@Entity(tableName = "salas")
data class Sala (

  @PrimaryKey(autoGenerate = true) val ID: Long = 0, //autoincrementa el id.
  val nombreSala: String,
  val capacidad: Long,
)
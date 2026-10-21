package benjaminv_isabel.app.salasmart

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


//contiene el CRUD.
@Dao
interface SalaDao {
  @Insert
  suspend fun insert(sala: Sala)

  @Update
  suspend fun update(sala: Sala)

  @Delete
  suspend fun delete(sala: Sala)

  @Query("SELECT * FROM SALAS")
  fun getAllSalas(): LiveData<List<Sala>>
}
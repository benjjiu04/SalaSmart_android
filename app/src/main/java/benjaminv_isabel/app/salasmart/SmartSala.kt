package benjaminv_isabel.app.salasmart

import androidx.room.Database
import androidx.room.RoomDatabase

//TODO: completar implementacion.
@Database(entities = [Sala::class], version = 1)
abstract class SmartSala: RoomDatabase() {
}
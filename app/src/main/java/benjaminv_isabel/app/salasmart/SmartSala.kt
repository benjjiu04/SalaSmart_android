package benjaminv_isabel.app.salasmart

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Sala::class], version = 1)
abstract class SmartSala: RoomDatabase() {
}
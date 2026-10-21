package benjaminv_isabel.app.salasmart

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

//TODO: completar implementacion.
@Database(entities = [Sala::class], version = 1, exportSchema = false)
abstract class SmartSalaBD: RoomDatabase() {

  abstract fun smartSalaDao(): SalaDao
  companion object{

    @Volatile
    private var INIT: SmartSalaBD? = null

    fun obtenerBD(contexto: Context): SmartSalaBD{
      return INIT?: synchronized(this){

        val init = Room.databaseBuilder(
          contexto.applicationContext,
          SmartSalaBD::class.java,
          "smart_sala_bd"
        ).build()
        INIT = init
        init
      }
    }
  }
}
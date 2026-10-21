package benjaminv_isabel.app.salasmart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ModeloSmartSala(private val salaDao: SalaDao) : ViewModel() {

  val todas_las_salas: LiveData<List<Sala>> = salaDao.getAllSalas()

  fun insert(sala: Sala){
    viewModelScope.launch{
      salaDao.insert(sala)
    }
  }

  fun delete(sala: Sala){
    viewModelScope.launch {
      salaDao.delete(sala)
    }
  }

  fun update(sala: Sala){
    viewModelScope.launch {
      salaDao.update(sala)
    }
  }
}
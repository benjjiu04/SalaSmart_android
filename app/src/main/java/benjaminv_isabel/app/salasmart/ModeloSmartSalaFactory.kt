package benjaminv_isabel.app.salasmart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ModeloSmartSalaFactory(private val salaDao: SalaDao) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ModeloSmartSala::class.java)) {
      return ModeloSmartSala(salaDao) as T
    }
    throw IllegalArgumentException("Clase ViewModel desconocida")
  }
}
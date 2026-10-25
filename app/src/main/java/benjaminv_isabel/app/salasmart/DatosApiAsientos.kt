package benjaminv_isabel.app.salasmart

import kotlinx.serialization.json.Json

/**
 * clase donde están los metodos para obtener, eliminar, y crear datos en las apis.
 *
 */
class DatosApiAsientos{

  companion object{
    private const val URL: String = "https://6add56c1fa5e73d4ab2df12a.mockapi.io/asientos/registro_asientos"

    fun<T> obtenerDatosApiAsientos(condicion: String?, valorCondicion: T?): ApiAsientos?{

      var urlFiltro = URL
      if (condicion != null && valorCondicion != null){
        urlFiltro = "?$condicion=$valorCondicion"
      }

      val peticionGet = PeticionHttp.get(urlFiltro)
      val dataApi = Json.decodeFromString<ApiAsientos>(peticionGet.respuestaBody)

      if (peticionGet.estatusPeticion.valor in 200..299) {
        return dataApi
      }

      return null
    }

    fun crearDatosApiAsientos(initApiAsientos: ApiAsientos): PeticionHttp.RespuestaHttp {

      val respuestaPeticionPost = PeticionHttp.post(URL, initApiAsientos)
      return respuestaPeticionPost
    }

    fun eliminarDatosApiAsientos(id: Long): PeticionHttp.RespuestaHttp{

      val urlFiltro = "$URL/$id"
      return PeticionHttp.delete(urlFiltro, id)

    }

  }
}
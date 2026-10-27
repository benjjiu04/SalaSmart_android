package benjaminv_isabel.app.salasmart

import kotlinx.serialization.json.Json

/**
 * clase donde están los metodos para obtener, eliminar, y crear datos en las apis.
 *
 */
class DatosApiAsientos{

  companion object{
    private const val URL: String = "https://6add56c1fa5e73d4ab2df12a.mockapi.io/asientos/registro_asientos"

    fun<T> obtenerDatosApiAsientos(condicion: String?, valorCondicion: T?): List<ApiAsientos>?{


      if (condicion != null && valorCondicion != null){
        val urlFiltro = "$URL?$condicion=$valorCondicion"

        val peticionGet = PeticionHttp.get(urlFiltro)
        //debe regresar una lista cuando en el url se pone el '?'
        val dataApi = Json.decodeFromString<List<ApiAsientos>>(peticionGet.respuestaBody)

        if (peticionGet.estatusPeticion.valor in 200..299) {
          return dataApi
        }
        return null
      }

      val peticionGet = PeticionHttp.get(URL)
      val dataAsientos = Json.decodeFromString<List<ApiAsientos>>(peticionGet.respuestaBody)

      if (peticionGet.estatusPeticion.valor in 200..299){
        return dataAsientos
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
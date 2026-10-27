package benjaminv_isabel.app.salasmart

import kotlinx.serialization.json.Json

class DatosApiAvisos {

  companion object{
    private const val URL: String = "https://6ade43dc4e88f1bba4a511a9.mockapi.io/avisos/registro_avisos"

    fun<T> obtenerDatosApiAvisos(condicion: String?, valorCondicion: T?): List<ApiAvisos>?{

      var urlFiltro = URL
      if (condicion != null && valorCondicion != null){
        urlFiltro = "?$condicion=$valorCondicion"
      }

      val peticionGet = PeticionHttp.get(urlFiltro)
      val dataApi = Json.decodeFromString<List<ApiAvisos>>(peticionGet.respuestaBody)

      if (peticionGet.estatusPeticion.valor in 200..299) {
        return dataApi
      }

      return null
    }

    fun crearDatosApiAvisos(initApiAvisos: ApiAvisos): PeticionHttp.RespuestaHttp {

      val respuestaPeticionPost = PeticionHttp.post(URL, initApiAvisos)
      return respuestaPeticionPost
    }

    fun eliminarDatosApiAvisos(id: Long): PeticionHttp.RespuestaHttp{

      val urlFiltro = "$URL/$id"
      return PeticionHttp.delete(urlFiltro, id)

    }

  }

}
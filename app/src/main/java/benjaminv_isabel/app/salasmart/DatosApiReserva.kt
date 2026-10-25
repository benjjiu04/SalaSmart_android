package benjaminv_isabel.app.salasmart

import kotlinx.serialization.json.Json

class DatosApiReserva {

  companion object{
    private const val URL: String = "https://6ade43dc4e88f1bba4a511a9.mockapi.io/registro_reservas/reservas"

    fun<T> obtenerDatosApiReserva(condicion: String?, valorCondicion: T?): ApiReserva?{

      var urlFiltro = URL
      if (condicion != null && valorCondicion != null){
        urlFiltro = "?$condicion=$valorCondicion"
      }

      val peticionGet = PeticionHttp.get(urlFiltro)
      val dataApi = Json.decodeFromString<ApiReserva>(peticionGet.respuestaBody)

      if (peticionGet.estatusPeticion.valor in 200..299) {
        return dataApi
      }

      return null
    }

    fun crearDatosApiReserva(initApiReserva: ApiReserva): PeticionHttp.RespuestaHttp {

      val respuestaPeticionPost = PeticionHttp.post(URL, initApiReserva)
      return respuestaPeticionPost
    }

    fun eliminarDatosApiReserva(id: Long): PeticionHttp.RespuestaHttp{

      val urlFiltro = "$URL/$id"
      return PeticionHttp.delete(urlFiltro, id)

    }

  }

}
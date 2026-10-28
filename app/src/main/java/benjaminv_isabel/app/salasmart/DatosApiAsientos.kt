package benjaminv_isabel.app.salasmart

/**
 * clase donde están los metodos para obtener, eliminar, y crear datos en las apis.
 */
class DatosApiAsientos{

  companion object{
    private const val URL: String = "https://6add56c1fa5e73d4ab2df12a.mockapi.io/asientos/registro_asientos"

    fun<T> obtenerDatosApiAsientos(condicion: String?, valorCondicion: T?): List<ApiAsientos>?{

      var urlFiltro = URL
      if (condicion != null && valorCondicion != null){
        // CORREGIDO: antes quedaba solo "?campo=valor" sin la URL base.
        urlFiltro = "$URL?$condicion=$valorCondicion"
      }

      val peticionGet = PeticionHttp.get(urlFiltro)

      if (peticionGet.estatusPeticion.valor in 200..299) {
        return JsonApi.json.decodeFromString<List<ApiAsientos>>(peticionGet.respuestaBody)
      }

      return null
    }

    fun crearDatosApiAsientos(initApiAsientos: ApiAsientos): PeticionHttp.RespuestaHttp {

      val respuestaPeticionPost = PeticionHttp.post(URL, initApiAsientos)
      return respuestaPeticionPost
    }

    fun eliminarDatosApiAsientos(id: Long): PeticionHttp.RespuestaHttp{
      // CORREGIDO: PeticionHttp.delete ya agrega "/$id", antes quedaba ".../id/id".
      return PeticionHttp.delete(URL, id)
    }

  }
}
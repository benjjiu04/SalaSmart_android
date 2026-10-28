package benjaminv_isabel.app.salasmart

class DatosApiAvisos {

  companion object{
    private const val URL: String = "https://6ade43dc4e88f1bba4a511a9.mockapi.io/avisos/registro_avisos"

    fun<T> obtenerDatosApiAvisos(condicion: String?, valorCondicion: T?): List<ApiAvisos>?{

      var urlFiltro = URL
      if (condicion != null && valorCondicion != null){
        // CORREGIDO: antes quedaba solo "?campo=valor" sin la URL base.
        urlFiltro = "$URL?$condicion=$valorCondicion"
      }

      val peticionGet = PeticionHttp.get(urlFiltro)

      if (peticionGet.estatusPeticion.valor in 200..299) {
        return JsonApi.json.decodeFromString<List<ApiAvisos>>(peticionGet.respuestaBody)
      }

      return null
    }

    fun crearDatosApiAvisos(initApiAvisos: ApiAvisos): PeticionHttp.RespuestaHttp {

      val respuestaPeticionPost = PeticionHttp.post(URL, initApiAvisos)
      return respuestaPeticionPost
    }

    fun eliminarDatosApiAvisos(id: Long): PeticionHttp.RespuestaHttp{
      // CORREGIDO: PeticionHttp.delete ya agrega "/$id", antes quedaba ".../id/id".
      return PeticionHttp.delete(URL, id)
    }

  }

}
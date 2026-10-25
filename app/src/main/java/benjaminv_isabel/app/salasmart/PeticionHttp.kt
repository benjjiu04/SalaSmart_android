package benjaminv_isabel.app.salasmart


import okhttp3.OkHttpClient
import okhttp3.Request

class PeticionHttp {

  enum class EstatusHttp(val valor: Int, val mensaje: String?){
    Ok(200, "la peticion fue procesada exitosamente"),
    BadRequest(400, "error 400: no se pudo procesar correctamente la peticion."),
    UnauthorizedError(401, "error 401: se requiere autenticación para poder acceder al contenido."),
    Forbidden(403, "error 403: usted no tiene permitido acceder a esta URL."),
    NotFound(404, "error 404: la pagina no existe o está mal escrita."),
    ServiceUnavailable(503, "error: 503: el servidor está temporalmente innacesible. intentelo luego.");

    internal fun guardarBodyJson(cuerpo: String): RespuestaHttp{
      return RespuestaHttp(this, cuerpo)
    }
  }

  data class RespuestaHttp(
    val estatusPeticion: EstatusHttp,
    val respuestaBody: String
  )


  companion object {
    fun get(URL: String): RespuestaHttp {

      val cliente = OkHttpClient()
      val peticion = Request.Builder()
        .url(URL)
        .get()
        .build()

      val miEnum = EstatusHttp.Ok
      var bodyRespuesta: RespuestaHttp
      cliente.newCall(peticion).execute().use { respuesta ->
        /*cuando la respuesta sea 'X' codigo de estatus tira una excepcion incluyendo el mensaje de dicho codigo.
         de lo contrario regresa la respuesta de la peticion.*/
        when (respuesta.code) {
          EstatusHttp.NotFound.valor -> throw Exception(EstatusHttp.NotFound.mensaje)
          EstatusHttp.BadRequest.valor -> throw Exception(EstatusHttp.BadRequest.mensaje)
          EstatusHttp.Forbidden.valor -> throw Exception(EstatusHttp.Forbidden.mensaje)
          EstatusHttp.ServiceUnavailable.valor -> throw Exception(EstatusHttp.ServiceUnavailable.mensaje)
          EstatusHttp.UnauthorizedError.valor -> throw Exception(EstatusHttp.UnauthorizedError.mensaje)
          EstatusHttp.Ok.valor -> bodyRespuesta = miEnum.guardarBodyJson(respuesta.body.string())
          else -> throw Exception("? error desconocido")
        }
        return RespuestaHttp(miEnum, bodyRespuesta.respuestaBody)
      }
    }

  }
}
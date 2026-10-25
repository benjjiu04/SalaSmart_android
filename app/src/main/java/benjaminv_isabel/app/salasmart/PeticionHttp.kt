package benjaminv_isabel.app.salasmart


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PeticionHttp {

  enum class EstatusHttp(val valor: Int, val mensaje: String?) {
    Ok(200, "la peticion fue procesada exitosamente."),
    Created(201, "la peticion fue creada exitosamente."),
    BadRequest(400, "error 400: no se pudo procesar correctamente la peticion."),
    UnauthorizedError(401, "error 401: se requiere autenticación para poder acceder al contenido."),
    Forbidden(403, "error 403: usted no tiene permitido acceder a esta URL."),
    NotFound(404, "error 404: la pagina no existe o está mal escrita."),
    ServiceUnavailable(
      503,
      "error: 503: el servidor está temporalmente innacesible. intentelo luego."
    );

    fun guardarBodyJson(cuerpo: String): RespuestaHttp {
      return RespuestaHttp(this, cuerpo)
    }

    companion object {
      // Esta función busca el enum exacto según el número de código HTTP
      fun buscarCodigo(codigo: Int): EstatusHttp? {
        return entries.find { it.valor == codigo }
      }
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


      cliente.newCall(peticion).execute().use { respuesta ->
        /*cuando la respuesta sea 'X' codigo de estatus tira una excepcion incluyendo el mensaje de dicho codigo.
         de lo contrario regresa la respuesta de la peticion.*/
        val bodyRespuesta = when (respuesta.code) {
          in 200..299 -> {
            val codigoEstatus = EstatusHttp.buscarCodigo(respuesta.code) ?: EstatusHttp.Ok
            codigoEstatus.guardarBodyJson(respuesta.body.string())
          }
          EstatusHttp.NotFound.valor -> throw Exception(EstatusHttp.NotFound.mensaje)
          EstatusHttp.BadRequest.valor -> throw Exception(EstatusHttp.BadRequest.mensaje)
          EstatusHttp.Forbidden.valor -> throw Exception(EstatusHttp.Forbidden.mensaje)
          EstatusHttp.ServiceUnavailable.valor -> throw Exception(EstatusHttp.ServiceUnavailable.mensaje)
          EstatusHttp.UnauthorizedError.valor -> throw Exception(EstatusHttp.UnauthorizedError.mensaje)

          else -> throw Exception("? error desconocido")
        }
        return bodyRespuesta
      }
    }


    inline fun<reified T> post(URL: String, objetoTipoJson: T): RespuestaHttp{

      val cliente = OkHttpClient()
      val mediaTypeHttp = "application/json; charset=utf-8".toMediaType()
      val stringJson = Json.encodeToString(objetoTipoJson)

      val cuerpoPost = stringJson.toRequestBody(mediaTypeHttp)

      val peticionPost = Request.Builder()
        .post(cuerpoPost)
        .url(URL)
        .build()

      cliente.newCall(peticionPost).execute().use { response ->

       val respuestaBody = when (response.code) {
         /*cuando los rango sean entre 200 y 299 no se imprimirá un mensaje personalizado,
          ya que haria que el enum sea enorme.
          se guardará el codigo en el enum para poder saber que codigo de estatus tuvo la peticion.*/
         in 200..299 -> {
           val codigoEstatus = EstatusHttp.buscarCodigo(response.code) ?: EstatusHttp.Created
           codigoEstatus.guardarBodyJson(response.body.string())

         }

         EstatusHttp.NotFound.valor -> throw Exception(EstatusHttp.NotFound.mensaje)
         EstatusHttp.BadRequest.valor -> throw Exception(EstatusHttp.BadRequest.mensaje)
         EstatusHttp.Forbidden.valor -> throw Exception(EstatusHttp.Forbidden.mensaje)
         EstatusHttp.ServiceUnavailable.valor -> throw Exception(EstatusHttp.ServiceUnavailable.mensaje)
         EstatusHttp.UnauthorizedError.valor -> throw Exception(EstatusHttp.UnauthorizedError.mensaje)
         else -> {
           val errorBody = response.body.string() ?: "sin cuerpo"
           throw Exception("? error desconocido. codigo de error ${response.code} - $errorBody")
         }
       }
        println("codigo de estatus: ${response.code}")
        return respuestaBody
      }
    }
  }
}
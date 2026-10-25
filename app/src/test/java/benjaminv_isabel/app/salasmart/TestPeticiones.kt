package benjaminv_isabel.app.salasmart

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter




class TestPeticiones {

  @Test
  fun postCorrecto() {
    //esto debe de enviar la peticion post y regresar el codigo de estatus 201
    val miPeticion = ApiAsientos("D1", true, null, LocalDate.now())
    val respuestaPost = PeticionHttp.post<ApiAsientos>("https://6add56c1fa5e73d4ab2df12a.mockapi.io/asientos/registro_asientos", miPeticion)

    assertEquals(201, respuestaPost.estatusPeticion.valor)
  }
}
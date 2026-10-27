package benjaminv_isabel.app.salasmart

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class DatosUsuario(
  val nombre: String,
  val edad: Int
)
@Serializable
data class ApiAsientos(
  val numeroAsiento: String,
  val estaDisponible: Boolean,
  val reservadoPor: DatosUsuario?,


  @Serializable(LocalDateSerializer::class)
  val fechaReserva: LocalDate?,

  val id: Long? = null,
)
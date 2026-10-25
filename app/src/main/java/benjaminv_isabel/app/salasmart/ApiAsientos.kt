package benjaminv_isabel.app.salasmart;

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate>{
  // Define el formato exacto en el que viene tu String de fecha en el JSON
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

  // De String (JSON) a Objeto LocalDate
  override fun deserialize(decoder: Decoder): LocalDate {
    val string = decoder.decodeString()
    return LocalDate.parse(string, formatter)
  }

  // De Objeto LocalDate a String (JSON)
  override fun serialize(encoder: Encoder, value: LocalDate) {
    val string = value.format(formatter)
    encoder.encodeString(string)
  }
}

@Serializable
data class ApiAsientos(
  val numeroAsiento: String,
  val estaDisponible: Boolean,
  val reservadoPor: String?,

  @Serializable(LocalDateSerializer::class)
  val fechaReserva: LocalDate?,

  val id: Long,
)
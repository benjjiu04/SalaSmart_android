package benjaminv_isabel.app.salasmart

import kotlinx.serialization.Serializable
import java.time.LocalDate

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

  // La API devuelve el id como texto ("1", "2"...), por eso es String y no Long.
  val id: String? = null,
)
package benjaminv_isabel.app.salasmart

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DatosUsuario(
  val nombre: String? = null,
  val edad: Int? = null
)
@Serializable
data class ApiAsientos(
  val numeroAsiento: String? = null,
  val estaDisponible: Boolean? = null,
  val reservadoPor: DatosUsuario? = null,

  @Serializable(LocalDateSerializer::class)
  val fechaReserva: LocalDate? = null,

  // La API devuelve el id como texto ("1", "2"...), por eso es String y no Long.
  val id: String? = null,
)
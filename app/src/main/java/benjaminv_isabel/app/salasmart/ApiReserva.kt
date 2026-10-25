package benjaminv_isabel.app.salasmart

import kotlinx.serialization.Serializable

@Serializable
class ApiReserva (
  val idAsiento: Long?,
  val nombre: String,
  val correo: String,
  val id: String? = null //los ids de los objetos json de la api se autoincrementan y siempre son strings.
)
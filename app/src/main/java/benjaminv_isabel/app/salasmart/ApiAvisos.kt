package benjaminv_isabel.app.salasmart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class ApiAvisos (
  // La API (mockapi) llama a este campo "servicioDisponible"; en Kotlin se sigue llamando estaDisponible.
  @SerialName("servicioDisponible")
  val estaDisponible: Boolean,
  val mensaje: String?,

  @Serializable(LocalDateSerializer::class)
  val fechaAviso: LocalDate?,

  @Serializable(LocalDateSerializer::class)
  val fechaTermino: LocalDate?,

  val id: String? = null //los ids de los objetos json de la api se autoincrementan y siempre son strings.
)
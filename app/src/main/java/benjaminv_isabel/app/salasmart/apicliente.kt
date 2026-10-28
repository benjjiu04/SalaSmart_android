package benjaminv_isabel.app.salasmart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

/** Configuración de JSON tolerante: ignora campos extra de la API. */
object JsonApi {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}

/**
 * Puente entre las pantallas y las clases DatosApiAsientos / DatosApiAvisos.
 * PeticionHttp usa red bloqueante, por eso todo corre en Dispatchers.IO
 * y devuelve Result (nunca revienta la app si falla la conexión).
 */
object ApiCliente {

    suspend fun avisos(): Result<List<ApiAvisos>> = withContext(Dispatchers.IO) {
        runCatching {
            DatosApiAvisos.obtenerDatosApiAvisos<String>(null, null) ?: emptyList()
        }
    }

    suspend fun asientos(): Result<List<ApiAsientos>> = withContext(Dispatchers.IO) {
        runCatching {
            DatosApiAsientos.obtenerDatosApiAsientos<String>(null, null) ?: emptyList()
        }
    }

    /** Publica en la API un registro por cada asiento reservado (solo el nombre, nunca el correo). */
    suspend fun registrarReserva(sala: String, numeros: Set<Int>, usuario: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                numeros.sorted().forEach { n ->
                    DatosApiAsientos.crearDatosApiAsientos(
                        ApiAsientos(
                            numeroAsiento = "$sala-$n",
                            estaDisponible = false,
                            reservadoPor = DatosUsuario(nombreDesdeCorreo(usuario), 0),
                            fechaReserva = LocalDate.now()
                        )
                    )
                }
            }
        }
}
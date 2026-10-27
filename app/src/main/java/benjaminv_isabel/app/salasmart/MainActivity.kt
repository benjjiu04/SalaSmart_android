package benjaminv_isabel.app.salasmart

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import benjaminv_isabel.app.salasmart.ui.theme.SalaSmartTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate


object Rutas {
    const val SALAS = "salas"
    const val LOGIN = "login"
    const val ASIENTOS = "asientos/{sala}"
    const val ADMIN = "admin"
    const val AVISOS = "avisos"
}

class MainActivity : ComponentActivity() {

    private val taskViewModel: ModeloSmartSala by viewModels {
        ModeloSmartSalaFactory(SmartSalaBD.obtenerBD(this).smartSalaDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalaSmartTheme {
                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf(false) }
                var correoActual by remember { mutableStateOf("") }

                var dataAvisos by remember { mutableStateOf<List<ApiAvisos>?>(null) }


                LaunchedEffect(Unit) {
                  try{
                    val resultadoApi = withContext(Dispatchers.IO) {
                      DatosApiAvisos.obtenerDatosApiAvisos(null, null)

                    }

                    dataAvisos = resultadoApi

                  }catch (err: Exception){
                    err.printStackTrace()
                  }
                }

                if (dataAvisos == null){
                  println("no hay ningun aviso.")
                }
                // TODO: reemplazar por los datos reales de ApiAvisos de tu compañero
                val avisos: List<Pair<String, String>> = remember(dataAvisos) {

                  dataAvisos?.mapNotNull { item ->

                    val mensaje = item.mensaje
                    if (mensaje.isNullOrBlank()) return@mapNotNull null

                    val fechaAviso = item.fechaAviso?.toString() ?: "Sin fecha"
                    val fechaTermino = item.fechaTermino?.toString() ?: "Sin fecha"

                    var mensajeVigencia = "vigencia del aviso $fechaAviso hasta $fechaTermino"

                    //si la fecha de hoy es mayor a la fecha de termino, se agrega al mensaje que el aviso esta caducado.
                    if (LocalDate.now() > item.fechaTermino){
                      mensajeVigencia += "\n\naviso caducado."
                    }
                    mensaje to mensajeVigencia

                  } ?: emptyList()
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Rutas.SALAS,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Rutas.SALAS) {
                            PantallaReservas(
                                estaLogueado = isLoggedIn,
                                correoActual = correoActual,
                                onIrALogin = { navController.navigate(Rutas.LOGIN) },
                                onCerrarSesion = {
                                    isLoggedIn = false
                                    correoActual = ""
                                },
                                onVerAsientos = { sala ->
                                    navController.navigate("asientos/${Uri.encode(sala.nombreSala)}")
                                },
                                onVerAvisos = { navController.navigate(Rutas.AVISOS) },
                                viewModel = taskViewModel
                            )
                        }

                        composable(Rutas.LOGIN) {
                            PantallaLogin(
                                onLoginSuccess = { correo ->
                                    isLoggedIn = true
                                    correoActual = correo
                                    navController.popBackStack()
                                },
                                onVolver = { navController.popBackStack() },
                                onAdminLogin = {
                                    navController.navigate(Rutas.ADMIN) {
                                        popUpTo(Rutas.SALAS)
                                    }
                                }
                            )
                        }

                        composable(
                            route = Rutas.ASIENTOS,
                            arguments = listOf(navArgument("sala") { type = NavType.StringType })
                        ) { entry ->
                            PantallaAsientos(
                                nombreSala = entry.arguments?.getString("sala") ?: "Sala",
                                usuario = correoActual,
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        composable(Rutas.ADMIN) {
                            PantallaAdmin(
                                viewModel = taskViewModel,
                                onVerAvisos = { navController.navigate(Rutas.AVISOS) },
                                onCerrarSesion = {
                                    navController.navigate(Rutas.SALAS) {
                                        popUpTo(Rutas.SALAS) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Rutas.AVISOS) {
                            PantallaAvisos(
                                avisos = avisos,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
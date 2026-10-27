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

                // TODO: reemplazar por los datos reales de ApiAvisos de tu compañero
                val avisos = remember {
                    listOf(
                        "Sala de Estudio en mantenimiento" to "Estará cerrada el viernes por la tarde.",
                        "Nuevo horario de atención" to "El campus abre desde las 7:30 de lunes a viernes.",
                        "Auditorio Central reservado" to "El miércoles habrá una charla abierta de 10:00 a 12:00."
                    )
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
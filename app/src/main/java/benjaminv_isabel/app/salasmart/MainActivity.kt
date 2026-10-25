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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Rutas.SALAS,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Rutas.SALAS) {
                            PantallaReservas(
                                estaLogueado = isLoggedIn,
                                onIrALogin = { navController.navigate(Rutas.LOGIN) },
                                onCerrarSesion = { isLoggedIn = false },
                                onVerAsientos = { sala ->
                                    navController.navigate("asientos/${Uri.encode(sala.nombreSala)}")
                                },
                                viewModel = taskViewModel
                            )
                        }

                        composable(Rutas.LOGIN) {
                            PantallaLogin(
                                onLoginSuccess = {
                                    isLoggedIn = true
                                    navController.popBackStack()
                                },
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Rutas.ASIENTOS,
                            arguments = listOf(navArgument("sala") { type = NavType.StringType })
                        ) { entry ->
                            PantallaAsientos(
                                nombreSala = entry.arguments?.getString("sala") ?: "Sala",
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
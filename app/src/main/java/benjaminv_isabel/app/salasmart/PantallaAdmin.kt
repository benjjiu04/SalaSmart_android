package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AzulOscuro = Color(0xFF0B1F4D)
private val Azul = Color(0xFF1E3A8A)
private val AzulBoton = Color(0xFF2563EB)
private val Violeta = Color(0xFF6366F1)
private val Verde = Color(0xFF16A34A)
private val Rojo = Color(0xFFDC2626)
private val Fondo = Color(0xFFF1F5FF)
private val TextoGris = Color(0xFF475569)

@Composable
fun PantallaAdmin(
    viewModel: ModeloSmartSala,
    onVerAvisos: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val salas by viewModel.todas_las_salas.observeAsState(emptyList())
    // Datos compartidos en Firestore (reservas de todos los teléfonos y sus correos)
    val asientos by remember { Nube.todosAsientos() }.collectAsState(initial = emptyList())
    val usuarios by remember { Nube.usuarios() }.collectAsState(initial = emptyList())

    val porSala = asientos.groupBy { it.sala }
    val porUsuario = asientos.groupingBy { it.usuario }.eachCount()

    var pestana by remember { mutableIntStateOf(0) }

    // Registro de asientos que viene de la API (ApiAsientos)
    var apiAsientos by remember { mutableStateOf<List<ApiAsientos>?>(null) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var apiCargando by remember { mutableStateOf(false) }
    var recargarApi by remember { mutableIntStateOf(0) }
    LaunchedEffect(recargarApi) {
        apiCargando = true
        apiError = null
        ApiCliente.asientos()
            .onSuccess { apiAsientos = it }
            .onFailure { apiError = it.message ?: "Error de conexión" }
        apiCargando = false
    }

    Column(Modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(Brush.linearGradient(listOf(AzulOscuro, Azul, Violeta)))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Panel de administrador", color = Color.White,
                            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Toda la base de datos de SalaSmart",
                            color = Color(0xFFE0E7FF), fontSize = 15.sp
                        )
                    }
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Salir") }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Dato("${salas.size} salas", AzulBoton)
                    Dato("${asientos.size} reservas", Rojo)
                    Dato("${usuarios.size} usuarios", Verde)
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        onClick = onVerAvisos,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Avisos") }
                }
            }
        }

        // ---------- PESTAÑAS ----------
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Pestana("Salas", pestana == 0, Modifier.weight(1f)) { pestana = 0 }
            Pestana("Reservas", pestana == 1, Modifier.weight(1f)) { pestana = 1 }
            Pestana("Usuarios", pestana == 2, Modifier.weight(1f)) { pestana = 2 }
            Pestana("API", pestana == 3, Modifier.weight(1f)) { pestana = 3 }
        }

        // ---------- CONTENIDO ----------
        when (pestana) {
            // ----- SALAS -----
            0 -> {
                if (salas.isEmpty()) {
                    Vacio("No hay salas en la base de datos.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(salas) { sala ->
                            val info = Aulas[sala.nombreSala]
                            val total = info?.asientos ?: sala.capacidad.toInt()
                            val ocupados = porSala[sala.nombreSala]?.size ?: 0
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            info?.nombre ?: sala.nombreSala, color = AzulOscuro,
                                            fontSize = 18.sp, fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${sala.nombreSala}  •  ${info?.ubicacion ?: ""}",
                                            color = TextoGris, fontSize = 14.sp
                                        )
                                        Text(
                                            "$ocupados de $total asientos ocupados",
                                            color = if (ocupados >= total) Rojo else Verde,
                                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    TextButton(
                                        onClick = {
                                            scope.launch { runCatching { Nube.liberarSala(sala.nombreSala) } }
                                        },
                                        enabled = ocupados > 0
                                    ) { Text("Liberar todo", color = Rojo) }
                                }
                            }
                        }
                    }
                }
            }

            // ----- RESERVAS (con perfil de quien reservó) -----
            1 -> {
                if (porSala.isEmpty()) {
                    Vacio("Todavía no hay reservas.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(porSala.entries.toList()) { entrada ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        Aulas[entrada.key]?.nombre ?: entrada.key,
                                        color = AzulOscuro,
                                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    entrada.value.sortedBy { it.numero }.forEach { a ->
                                        Row(
                                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Asiento ${a.numero}", color = AzulOscuro,
                                                fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.width(100.dp)
                                            )
                                            if (a.usuario.isBlank()) {
                                                Text(
                                                    "Sin registro", color = TextoGris, fontSize = 15.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            } else {
                                                PerfilUsuario(
                                                    correo = a.usuario,
                                                    modifier = Modifier.weight(1f),
                                                    tamano = 38.dp
                                                )
                                            }
                                            TextButton(onClick = {
                                                scope.launch {
                                                    runCatching { Nube.liberarAsiento(a.sala, a.numero) }
                                                }
                                            }) { Text("Liberar", color = Rojo) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----- USUARIOS (con perfil) -----
            2 -> {
                if (usuarios.isEmpty()) {
                    Vacio("Todavía no hay usuarios registrados.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(usuarios) { u ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PerfilUsuario(
                                        correo = u.correo,
                                        modifier = Modifier.weight(1f),
                                        tamano = 52.dp
                                    )
                                    Text(
                                        "${porUsuario[u.correo] ?: 0} asientos",
                                        color = AzulBoton, fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ----- API (registro de asientos) -----
            else -> {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Registro de asientos (API)", color = AzulOscuro,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { recargarApi++ }) { Text("Actualizar", color = AzulBoton) }
                }
                when {
                    apiCargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AzulBoton)
                    }
                    apiError != null -> Vacio("No se pudo leer la API: $apiError")
                    apiAsientos.isNullOrEmpty() -> Vacio("La API no tiene registros.")
                    else -> LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(apiAsientos.orEmpty()) { a ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "Asiento ${a.numeroAsiento}", color = AzulOscuro,
                                            fontSize = 17.sp, fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            a.reservadoPor?.let { "Reservado por ${it.nombre}" } ?: "Sin reserva",
                                            color = TextoGris, fontSize = 14.sp
                                        )
                                        if (a.fechaReserva != null) {
                                            Text("Fecha: ${a.fechaReserva}", color = TextoGris, fontSize = 14.sp)
                                        }
                                    }
                                    Text(
                                        if (a.estaDisponible == true) "Disponible" else "Ocupado",
                                        color = if (a.estaDisponible == true) Verde else Rojo,
                                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Vacio(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(texto, color = TextoGris, fontSize = 17.sp)
    }
}

@Composable
private fun Dato(texto: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color) {
        Text(
            texto, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun Pestana(texto: String, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (activa) AzulBoton else Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Text(
            texto,
            color = if (activa) Color.White else AzulOscuro,
            fontSize = 16.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()
        )
    }
}
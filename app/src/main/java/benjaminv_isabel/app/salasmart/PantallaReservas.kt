package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val AzulOscuro = Color(0xFF0B1F4D)
private val Azul = Color(0xFF1E3A8A)
private val AzulBoton = Color(0xFF2563EB)
private val Violeta = Color(0xFF6366F1)
private val Verde = Color(0xFF16A34A)
private val Rojo = Color(0xFFDC2626)
private val Fondo = Color(0xFFF1F5FF)
private val TextoGris = Color(0xFF475569)

@Composable
fun PantallaReservas(
    modifier: Modifier = Modifier,
    estaLogueado: Boolean = false,
    onIrALogin: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onVerAsientos: (Sala) -> Unit = {},
    viewModel: ModeloSmartSala = viewModel()
) {
    val listaSalas by viewModel.todas_las_salas.observeAsState(initial = emptyList())

    // Asientos reservados de todas las salas (Room, se actualiza solo)
    val contexto = LocalContext.current
    val asientosLive = remember { AsientosBD.obtener(contexto).asientoDao().observarTodos() }
    val todosAsientos by asientosLive.observeAsState(initial = emptyList())
    val ocupadosPorSala = todosAsientos.groupingBy { it.sala }.eachCount()

    fun totalDe(s: Sala): Int = Aulas[s.nombreSala]?.asientos ?: s.capacidad.toInt()
    fun estaLlena(s: Sala): Boolean = (ocupadosPorSala[s.nombreSala] ?: 0) >= totalDe(s)

    val llenas = listaSalas.count { estaLlena(it) }
    val conCupo = listaSalas.size - llenas

    Column(modifier = modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(Brush.linearGradient(listOf(AzulOscuro, Azul, Violeta)))
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            "SalaSmart", color = Color.White,
                            fontSize = 30.sp, fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            if (estaLogueado) "Sesión iniciada" else "Reserva tu sala en segundos",
                            color = Color(0xFFE0E7FF), fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { if (estaLogueado) onCerrarSesion() else onIrALogin() },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            if (estaLogueado) "Cerrar sesión" else "Iniciar sesión",
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumenChip("$conCupo salas con cupo", Verde)
                    ResumenChip("$llenas llenas", Rojo)
                }
            }
        }

        // ---------- CONTENIDO ----------
        // Solo agrega las salas que todavía no existen (no duplica)
        val faltantes = Aulas.keys.filter { clave -> listaSalas.none { it.nombreSala == clave } }
        val cargarEjemplo = {
            faltantes.forEach { clave ->
                viewModel.insert(
                    Sala(nombreSala = clave, capacidad = (Aulas[clave]?.asientos ?: 10).toLong())
                )
            }
        }

        if (listaSalas.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hay salas registradas.", color = TextoGris, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { cargarEjemplo() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBoton)
                ) { Text("Cargar salas de ejemplo", fontSize = 16.sp) }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Salas",
                    color = AzulOscuro, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (faltantes.isNotEmpty()) {
                    TextButton(onClick = { cargarEjemplo() }) {
                        Text("+ Agregar ${faltantes.size} salas de ejemplo", color = AzulBoton)
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listaSalas) { sala ->
                    TarjetaSala(
                        sala = sala,
                        ocupados = ocupadosPorSala[sala.nombreSala] ?: 0
                    ) {
                        if (!estaLogueado) onIrALogin() else onVerAsientos(sala)
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenChip(texto: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color) {
        Text(
            texto, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun TarjetaSala(sala: Sala, ocupados: Int, onClick: () -> Unit) {
    val info = Aulas[sala.nombreSala]
        ?: InfoAula(sala.nombreSala, "", R.drawable.sala1, sala.capacidad.toInt())
    val libres = (info.asientos - ocupados).coerceAtLeast(0)
    val llena = libres == 0
    val estado = if (llena) Rojo else Verde

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // ----- Foto de la sala -----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(info.foto),
                    contentDescription = info.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            )
                        )
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = estado,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Text(
                        if (llena) "Llena" else "Disponible",
                        color = Color.White, fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            // ----- Información -----
            Column(Modifier.padding(16.dp)) {
                Text(
                    info.nombre, color = AzulOscuro,
                    fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
                )
                if (info.ubicacion.isNotEmpty()) {
                    Text(info.ubicacion, color = TextoGris, fontSize = 15.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (llena) "Sin asientos libres (${info.asientos} en total)"
                    else "$libres de ${info.asientos} asientos libres",
                    color = estado, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBoton),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Ver asientos  →", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
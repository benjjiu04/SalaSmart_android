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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AsientosBD.obtener(contexto).asientoDao() }
    val asientosLive = remember { dao.observarTodos() }

    val salas by viewModel.todas_las_salas.observeAsState(emptyList())
    val asientos by asientosLive.observeAsState(emptyList())
    val porSala = asientos.groupBy { it.sala }

    var pestana by remember { mutableIntStateOf(0) }

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
                        Text("Panel de administrador", color = Color.White,
                            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Toda la base de datos de SalaSmart",
                            color = Color(0xFFE0E7FF), fontSize = 15.sp)
                    }
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Salir") }
                }
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Dato("${salas.size} salas", AzulBoton)
                    Dato("${asientos.size} asientos reservados", Rojo)
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
            Pestana("Avisos", false, Modifier.weight(1f)) { onVerAvisos() }
        }

        // ---------- CONTENIDO ----------
        if (pestana == 0) {
            if (salas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay salas en la base de datos.", color = TextoGris, fontSize = 17.sp)
                }
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
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(info?.nombre ?: sala.nombreSala, color = AzulOscuro,
                                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("${sala.nombreSala}  •  ${info?.ubicacion ?: ""}",
                                        color = TextoGris, fontSize = 14.sp)
                                    Text(
                                        "$ocupados de $total asientos ocupados",
                                        color = if (ocupados >= total) Rojo else Verde,
                                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TextButton(
                                    onClick = { scope.launch { dao.liberar(sala.nombreSala) } },
                                    enabled = ocupados > 0
                                ) { Text("Liberar", color = Rojo) }
                            }
                        }
                    }
                }
            }
        } else {
            if (porSala.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Todavía no hay reservas.", color = TextoGris, fontSize = 17.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(porSala.entries.toList()) { (nombre, lista) ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(Aulas[nombre]?.nombre ?: nombre, color = AzulOscuro,
                                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Asientos: " + lista.map { it.numero }.sorted().joinToString(", "),
                                    color = TextoGris, fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Dato(texto: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color) {
        Text(texto, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
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
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
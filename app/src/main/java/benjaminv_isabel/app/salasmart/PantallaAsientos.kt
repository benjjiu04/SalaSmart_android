package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Azul = Color(0xFF1E3A8A)
private val Celeste = Color(0xFF3B82F6)
private val Morado = Color(0xFF7C3AED)
private val Verde = Color(0xFF22C55E)
private val Rojo = Color(0xFFEF4444)
private val Naranja = Color(0xFFF59E0B)
private val Fondo = Color(0xFFEEF2FF)

@Composable
fun PantallaAsientos(
    nombreSala: String,
    onVolver: () -> Unit,
    totalAsientos: Int = 40
) {
    val clave = remember(nombreSala) { RepositorioAsientos.claveSala(nombreSala) }
    val scope = rememberCoroutineScope()

    var reservados by remember { mutableStateOf(setOf<Int>()) }
    var seleccionados by remember { mutableStateOf(setOf<Int>()) }
    var mensaje by remember { mutableStateOf("") }
    var conectado by remember { mutableStateOf(true) }

    // Consulta la base compartida cada 2 segundos
    LaunchedEffect(clave) {
        while (true) {
            val remoto = RepositorioAsientos.leer(clave)
            if (remoto != null) {
                reservados = remoto
                seleccionados = seleccionados - remoto
                conectado = true
            } else {
                conectado = false
            }
            delay(2000)
        }
    }

    val libres = totalAsientos - reservados.size

    Column(modifier = Modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Azul, Morado)),
                    RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onVolver, contentPadding = PaddingValues(0.dp)) {
                        Text("← Volver", color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.2f)) {
                        Text(
                            if (conectado) "● En vivo" else "● Sin conexión",
                            color = if (conectado) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier.size(84.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) { Text("💺", fontSize = 44.sp) }
                Spacer(Modifier.height(8.dp))
                Text(nombreSala, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Contador("$libres libres", Verde)
                    Contador("${reservados.size} ocupados", Rojo)
                }
            }
        }

        // ---------- LEYENDA ----------
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Leyenda(Verde, "Disponible")
            Leyenda(Rojo, "Ocupado")
            Leyenda(Naranja, "Tu selección")
        }

        // ---------- MAPA ----------
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Celeste, Morado)))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🖥  FRENTE DE LA SALA", color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(4.dp))

            val filas = (totalAsientos + 3) / 4
            for (fila in 0 until filas) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (col in 0 until 4) {
                        val numero = fila * 4 + col + 1
                        if (col == 2) Spacer(Modifier.width(34.dp)) // pasillo
                        if (numero <= totalAsientos) {
                            val color = when {
                                numero in reservados -> Rojo
                                numero in seleccionados -> Naranja
                                else -> Verde
                            }
                            Asiento(numero, color, numero in reservados) {
                                seleccionados = if (numero in seleccionados)
                                    seleccionados - numero else seleccionados + numero
                                mensaje = ""
                            }
                        } else {
                            Spacer(Modifier.size(62.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ---------- BARRA INFERIOR ----------
        Surface(
            shadowElevation = 16.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text(
                    if (seleccionados.isEmpty()) "Toca un asiento verde para elegirlo"
                    else "Elegiste: ${seleccionados.sorted().joinToString(", ")}",
                    color = Color.DarkGray, fontSize = 15.sp, fontWeight = FontWeight.Medium
                )
                if (mensaje.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(mensaje, color = Azul, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val ocupados = RepositorioAsientos.reservar(clave, seleccionados)
                            if (ocupados.isEmpty()) {
                                mensaje = "¡Reserva confirmada!"
                                seleccionados = emptySet()
                            } else {
                                mensaje = "Asiento(s) ${ocupados.sorted().joinToString(", ")} ya ocupado(s) o sin conexión"
                                seleccionados = seleccionados - ocupados
                            }
                            RepositorioAsientos.leer(clave)?.let { reservados = it }
                        }
                    },
                    enabled = seleccionados.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Confirmar reserva", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            RepositorioAsientos.reiniciar(clave)
                            reservados = emptySet()
                            seleccionados = emptySet()
                            mensaje = "Sala reiniciada"
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Liberar todos los asientos (demo)", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun Asiento(numero: Int, color: Color, ocupado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.75f))))
            .clickable(enabled = !ocupado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (ocupado) "✖" else "$numero",
            color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun Contador(texto: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color) {
        Text(
            texto, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
    }
}
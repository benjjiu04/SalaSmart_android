package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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

private val AzulOscuro = Color(0xFF1E40AF)
private val AzulMedio = Color(0xFF2563EB)
private val Violeta = Color(0xFF6366F1)
private val Verde = Color(0xFF16A34A)
private val Rojo = Color(0xFFEF4444)
private val Seleccion = Color(0xFF2563EB)
private val Fondo = Color(0xFFF1F5FF)
private val PisoSala = Color(0xFFE2E8F5)
private val TextoGris = Color(0xFF64748B)

@Composable
fun PantallaAsientos(
    nombreSala: String,
    onVolver: () -> Unit,
    ubicacion: String = "Edificio A • Piso 1",
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

    Column(modifier = Modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(Brush.linearGradient(listOf(AzulOscuro, AzulMedio, Violeta)))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onVolver, contentPadding = PaddingValues(0.dp)) {
                        Text("←  Volver", color = Color.White, fontSize = 16.sp)
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
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) { Text("💺", fontSize = 38.sp) }
                Spacer(Modifier.height(6.dp))
                Text(
                    nombreSala, color = Color.White,
                    fontSize = 34.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "📍 $ubicacion", color = Color(0xFFE0E7FF),
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        // ---------- CONTENIDO CON SCROLL ----------
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------- LEYENDA ----------
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Leyenda(Verde, "Disponible")
                    Leyenda(Rojo, "Ocupado")
                    Leyenda(Seleccion, "Tu selección")
                }
            }

            // ---------- SALÓN ----------
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(PisoSala)
                        .fillMaxWidth()
                ) {
                    // Zona del frente: planta, pizarra, escritorio
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD5DEEF))
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪴", fontSize = 34.sp)
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .weight(3f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(3.dp, Color(0xFF94A3B8), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "PIZARRA", color = TextoGris, fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text("🧑‍🏫", fontSize = 34.sp)
                    }

                    // Filas de pupitres
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val filas = (totalAsientos + 3) / 4
                        for (fila in 0 until filas) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (col in 0 until 4) {
                                    val numero = fila * 4 + col + 1
                                    if (col == 2) Spacer(Modifier.weight(0.7f)) // pasillo
                                    Box(Modifier.weight(1f)) {
                                        if (numero <= totalAsientos) {
                                            val ocupado = numero in reservados
                                            val color = when {
                                                ocupado -> Rojo
                                                numero in seleccionados -> Seleccion
                                                else -> Verde
                                            }
                                            Pupitre(numero, color, ocupado) {
                                                seleccionados =
                                                    if (numero in seleccionados) seleccionados - numero
                                                    else seleccionados + numero
                                                mensaje = ""
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------- BARRA INFERIOR ----------
        Surface(
            shadowElevation = 16.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBE7FF)),
                        contentAlignment = Alignment.Center
                    ) { Text("💺", fontSize = 22.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (seleccionados.isEmpty()) "Selecciona un asiento"
                            else "Elegiste: ${seleccionados.sorted().joinToString(", ")}",
                            color = AzulOscuro, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Text(
                            if (mensaje.isNotEmpty()) mensaje
                            else "Para reservar tu espacio en la sala.",
                            color = TextoGris, fontSize = 13.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5FF),
                        border = BorderStroke(1.dp, Color(0xFFD6E0F5))
                    ) {
                        Text(
                            "Libres: ${totalAsientos - reservados.size}/$totalAsientos",
                            color = AzulMedio, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

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
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMedio),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
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

/** Pupitre blanco con el número del asiento en una etiqueta de color. */
@Composable
private fun Pupitre(numero: Int, color: Color, ocupado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFF))
            .border(1.5.dp, Color(0xFFD0DAEE), RoundedCornerShape(14.dp))
            .clickable(enabled = !ocupado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.85f), color))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (ocupado) "✖" else "$numero",
                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 13.sp, color = AzulOscuro, fontWeight = FontWeight.Medium)
    }
}
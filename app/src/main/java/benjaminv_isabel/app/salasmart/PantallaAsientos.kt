package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AzulOscuro = Color(0xFF0B1F4D)
private val AzulMedio = Color(0xFF2563EB)
private val Verde = Color(0xFF16A34A)
private val Rojo = Color(0xFFDC2626)
private val Seleccion = Color(0xFF2563EB)
private val Fondo = Color(0xFFF1F5FF)
private val PisoSala = Color(0xFFE2E8F5)
private val TextoGris = Color(0xFF475569)
private val Madera = Color(0xFFB7791F)


@Composable
fun PantallaAsientos(
    nombreSala: String,
    onVolver: () -> Unit
) {
    val info = Aulas[nombreSala]
        ?: InfoAula(nombreSala, "Edificio A • Piso 1", R.drawable.sala1, 40)
    val totalAsientos = info.asientos

    val scope = rememberCoroutineScope()
    val contexto = LocalContext.current
    // Base de datos propia de asientos (no toca SmartSalaBD)
    val dao = remember { AsientosBD.obtener(contexto).asientoDao() }

    // Asientos ocupados guardados en Room (se actualiza solo)
    val reservados = dao.observar(nombreSala).observeAsState(emptyList()).value.toSet()
    var seleccionados by remember { mutableStateOf(setOf<Int>()) }
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO CON FOTO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
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
                                Color.Black.copy(alpha = 0.35f),
                                AzulOscuro.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Surface(
                onClick = onVolver,
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Text(
                    "←  Volver", color = Color.White, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = Verde,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text(
                    "${totalAsientos - reservados.size} libres",
                    color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
            ) {
                Text(
                    info.nombre, color = Color.White,
                    fontSize = 30.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "$nombreSala  •  ${info.ubicacion}",
                    color = Color(0xFFE0E7FF), fontSize = 16.sp
                )
            }
        }

        // ---------- CONTENIDO CON SCROLL ----------
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Leyenda
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Leyenda(Verde, "Disponible")
                    Leyenda(Rojo, "Ocupado")
                    Leyenda(Seleccion, "Tu selección")
                }
            }

            // Salón
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
                    // Frente de la sala: pizarra y escritorio dibujados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD5DEEF))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(3f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(3.dp, Color(0xFF94A3B8), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "PIZARRA", color = TextoGris, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Madera),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Profesor", color = Color.White, fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Filas de asientos
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
                    ) {
                        Text(
                            "${seleccionados.size}", color = AzulMedio,
                            fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (seleccionados.isEmpty()) "Selecciona un asiento"
                            else "Elegiste: ${seleccionados.sorted().joinToString(", ")}",
                            color = AzulOscuro, fontWeight = FontWeight.Bold, fontSize = 17.sp
                        )
                        Text(
                            mensaje.ifEmpty { "Para reservar tu espacio en la sala." },
                            color = TextoGris, fontSize = 14.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5FF),
                        border = BorderStroke(1.dp, Color(0xFFD6E0F5))
                    ) {
                        Text(
                            "Libres: ${totalAsientos - reservados.size}/$totalAsientos",
                            color = AzulMedio, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            dao.insertar(seleccionados.map { Asiento(nombreSala, it) })
                            mensaje = "¡Reserva confirmada!"
                            seleccionados = emptySet()
                        }
                    },
                    enabled = seleccionados.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMedio),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Confirmar reserva", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        scope.launch {
                            dao.liberar(nombreSala)
                            seleccionados = emptySet()
                            mensaje = "Sala reiniciada"
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Liberar todos los asientos (demo)", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

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
                .fillMaxWidth(0.75f)
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.85f), color))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (ocupado) "X" else "$numero",
                color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.Medium)
    }
}
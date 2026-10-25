package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Azul = Color(0xFF1E3A8A)
private val Celeste = Color(0xFF3B82F6)
private val Verde = Color(0xFF16A34A)
private val Rojo = Color(0xFFDC2626)
private val Fondo = Color(0xFFF3F4F6)

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

    Column(modifier = modifier.fillMaxSize().background(Fondo)) {

        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Azul, Celeste)),
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(horizontal = 24.dp, vertical = 36.dp)
        ) {
            Column {
                Text(
                    "SalaSmart", color = Color.White,
                    fontSize = 30.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Reserva tu sala en segundos",
                    color = Color(0xFFDBEAFE), fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { if (estaLogueado) onCerrarSesion() else onIrALogin() },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(if (estaLogueado) "Cerrar sesión" else "Iniciar sesión / Crear cuenta")
                }
                Spacer(Modifier.height(12.dp))

                val libres = listaSalas.count { it.capacidad > 0 }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumenChip("$libres disponibles")
                    ResumenChip("${listaSalas.size - libres} reservadas")
                }
            }
        }

        // ---------- LISTA ----------
        if (listaSalas.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hay salas registradas.", color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.insert(Sala(nombreSala = "Sala 1", capacidad = 10L))
                        viewModel.insert(Sala(nombreSala = "Sala 2", capacidad = 6L))
                        viewModel.insert(Sala(nombreSala = "Sala 3", capacidad = 4L))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul)
                ) {
                    Text("Cargar salas de ejemplo")
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(listaSalas) { sala ->
                    val esDisponible = sala.capacidad > 0

                    TarjetaSala(sala = sala, esDisponible = esDisponible) {
                        if (!estaLogueado) onIrALogin() else onVerAsientos(sala)
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenChip(texto: String) {
    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.2f)) {
        Text(
            texto, color = Color.White, fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TarjetaSala(sala: Sala, esDisponible: Boolean, onClick: () -> Unit) {
    val color = if (esDisponible) Verde else Rojo

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (esDisponible) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                    contentDescription = null, tint = color
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(sala.nombreSala, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Person, null,
                        tint = Color.Gray, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Capacidad: ${sala.capacidad}", color = Color.Gray, fontSize = 13.sp)
                }
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.12f)) {
                    Text(
                        if (esDisponible) "Disponible" else "Reservada",
                        color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Azul)
            ) {
                Text("Ver asientos")
            }
        }
    }
}
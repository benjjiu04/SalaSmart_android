package benjaminv_isabel.app.salasmart


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val AzulOscuro = Color(0xFF0B1F4D)
private val Azul = Color(0xFF1E3A8A)
private val AzulBoton = Color(0xFF2563EB)
private val Violeta = Color(0xFF6366F1)
private val Fondo = Color(0xFFF1F5FF)
private val TextoGris = Color(0xFF475569)

// avisos = lista de (título, descripción)
@Composable
fun PantallaAvisos(
    avisos: List<Pair<String, AnnotatedString>>,
    cargando: Boolean = false,
    error: String? = null,
    onReintentar: () -> Unit = {},
    onVolver: () -> Unit
) {
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
                Surface(
                    onClick = onVolver,
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        "←  Volver", color = Color.White, fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Avisos del campus", color = Color.White,
                    fontSize = 30.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Novedades y mantenciones de las salas",
                    color = Color(0xFFE0E7FF), fontSize = 16.sp
                )
            }
        }

        // ---------- CONTENIDO ----------
        when {
            cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AzulBoton)
            }

            error != null -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No se pudieron cargar los avisos", color = AzulOscuro,
                    fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(error, color = TextoGris, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onReintentar,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBoton)
                ) { Text("Reintentar") }
            }

            avisos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay avisos por ahora.", color = TextoGris, fontSize = 18.sp)
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(avisos) { indice, aviso ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBE7FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${indice + 1}", color = AzulBoton,
                                    fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(aviso.first, color = AzulOscuro,
                                    fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))

                                Text(aviso.second, color = TextoGris, fontSize = 15.sp)

                            }
                        }
                    }
                }
            }
        }
    }
}
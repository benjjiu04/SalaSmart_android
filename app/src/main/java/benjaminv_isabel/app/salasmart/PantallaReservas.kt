package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PantallaReservas(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Inicializamos la base de datos (SmartSala es como la llamó tu compañero)
    val db = remember { SmartSala.getDatabase(context) }
    val salaDao = db.salaDao()

    // 2. Leemos las salas en tiempo real
    val listaSalas by salaDao.obtenerSalas().collectAsState(initial = emptyList())

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Reserva de Salas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Si la lista está vacía, mostramos un mensaje
        if (listaSalas.isEmpty()) {
            Text("No hay salas registradas. Pídele a tu compañero que inserte datos de prueba.")
        } else {
            LazyColumn {
                items(listaSalas) { sala ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                // Asumimos que tu compañero usó "nombre" y "estado".
                                // Si te sale rojo, cámbialo por sala.nombreSala o sala.estadoSala
                                Text(text = sala.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Estado: ${sala.estado}")
                            }

                            Button(
                                onClick = {
                                    val nuevoEstado = if (sala.estado == "Disponible") "Reservada" else "Disponible"
                                    coroutineScope.launch {
                                        // Si la función de tu compañero se llama diferente, cámbialo aquí
                                        salaDao.actualizarEstado(sala.id, nuevoEstado)
                                    }
                                }
                            ) {
                                Text(if (sala.estado == "Disponible") "Reservar" else "Liberar")
                            }
                        }
                    }
                }
            }
        }
    }
}
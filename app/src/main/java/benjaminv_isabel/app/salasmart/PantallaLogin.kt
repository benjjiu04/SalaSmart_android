package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Azul = Color(0xFF1E3A8A)
private val Celeste = Color(0xFF3B82F6)
private val Fondo = Color(0xFFF3F4F6)

@Composable
fun PantallaLogin(
    onLoginSuccess: () -> Unit,
    onVolver: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var esRegistro by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
    ) {
        // ---------- ENCABEZADO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Azul, Color(0xFF7C3AED))),
                    RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .padding(horizontal = 24.dp, vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.White.copy(alpha = 0.18f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💺", fontSize = 80.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text("🖥  🪑  📋  🪑  🖥", fontSize = 26.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "SalaSmart", color = Color.White,
                    fontSize = 38.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(
                    if (esRegistro) "Crea tu cuenta para reservar"
                    else "Inicia sesión para reservar",
                    color = Color(0xFFE0E7FF), fontSize = 16.sp
                )
            }
        }

        // ---------- FORMULARIO ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        if (esRegistro) "Crear cuenta" else "Iniciar sesión",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Azul
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = "" },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = "" },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (esRegistro) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmar,
                            onValueChange = { confirmar = it; error = "" },
                            label = { Text("Confirmar contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(error, color = Color(0xFFDC2626), fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            when {
                                email.isBlank() || password.isBlank() ->
                                    error = "Completa todos los campos"
                                esRegistro && password != confirmar ->
                                    error = "Las contraseñas no coinciden"
                                else -> onLoginSuccess()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Azul),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (esRegistro) "Crear cuenta" else "Iniciar sesión")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { esRegistro = !esRegistro; error = "" }) {
                Text(
                    if (esRegistro) "¿Ya tienes cuenta? Inicia sesión"
                    else "¿No tienes cuenta? Crear cuenta",
                    color = Azul
                )
            }

            TextButton(onClick = onVolver) {
                Text("← Volver a las salas", color = Color.Gray)
            }
        }
    }
}
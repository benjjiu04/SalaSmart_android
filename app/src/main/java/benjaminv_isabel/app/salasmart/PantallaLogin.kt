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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF0B1F4D)
private val Azul = Color(0xFF1E3A8A)
private val AzulBoton = Color(0xFF2563EB)
private val Celeste = Color(0xFF60A5FA)
private val Fondo = Color(0xFFF1F5FF)
private val BordeCampo = Color(0xFFD6E0F5)
private val TextoGris = Color(0xFF64748B)

// Cuenta de administrador (solo para la demo del curso)
private const val ADMIN_CORREO = "admin@salasmart.cl"
private const val ADMIN_CLAVE = "admin123"

@Composable
fun PantallaLogin(
    onLoginSuccess: () -> Unit,
    onVolver: () -> Unit = {},
    onAdminLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var esRegistro by remember { mutableStateOf(false) }
    var verPassword by remember { mutableStateOf(false) }
    var mantener by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    val transformacion =
        if (verPassword) VisualTransformation.None else PasswordVisualTransformation()

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
                .clip(RoundedCornerShape(bottomStart = 56.dp, bottomEnd = 56.dp))
                .background(Brush.verticalGradient(listOf(AzulOscuro, Azul, AzulBoton)))
                .padding(top = 48.dp, bottom = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Círculo del logo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Celeste, AzulBoton))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💺", fontSize = 56.sp)
                }
                Spacer(Modifier.height(14.dp))

                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White)) { append("Sala") }
                        withStyle(SpanStyle(color = Celeste)) { append("Smart") }
                    },
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Reserva y gestiona los espacios\nde forma rápida y sencilla.",
                    color = Color(0xFFE0E7FF),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ---------- TARJETA (se sobrepone al encabezado) ----------
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-48).dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    if (esRegistro) "Crear cuenta" else "Iniciar sesión",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (esRegistro) "Regístrate para reservar tus espacios."
                    else "Accede a tu cuenta para reservar tus espacios.",
                    color = TextoGris,
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(22.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; error = "" },
                    placeholder = { Text("Correo electrónico") },
                    leadingIcon = { Text("✉️", fontSize = 20.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BordeCampo,
                        focusedBorderColor = AzulBoton,
                        unfocusedContainerColor = Color(0xFFF8FAFF),
                        focusedContainerColor = Color(0xFFF8FAFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = "" },
                    placeholder = { Text("Contraseña") },
                    leadingIcon = { Text("🔒", fontSize = 20.sp) },
                    trailingIcon = {
                        Text(
                            if (verPassword) "🙈" else "👁",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { verPassword = !verPassword }
                                .padding(8.dp)
                        )
                    },
                    visualTransformation = transformacion,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BordeCampo,
                        focusedBorderColor = AzulBoton,
                        unfocusedContainerColor = Color(0xFFF8FAFF),
                        focusedContainerColor = Color(0xFFF8FAFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (esRegistro) {
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it; error = "" },
                        placeholder = { Text("Confirmar contraseña") },
                        leadingIcon = { Text("🔒", fontSize = 20.sp) },
                        visualTransformation = transformacion,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BordeCampo,
                            focusedBorderColor = AzulBoton,
                            unfocusedContainerColor = Color(0xFFF8FAFF),
                            focusedContainerColor = Color(0xFFF8FAFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color(0xFFDC2626), fontSize = 14.sp)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        when {
                            email.isBlank() || password.isBlank() ->
                                error = "Completa todos los campos"
                            esRegistro && password != confirmar ->
                                error = "Las contraseñas no coinciden"
                            // Inicio de sesión como administrador
                            !esRegistro &&
                                    email.trim().lowercase() == ADMIN_CORREO &&
                                    password == ADMIN_CLAVE ->
                                onAdminLogin()
                            else -> onLoginSuccess()
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBoton),
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                ) {
                    Text(
                        if (esRegistro) "Crear cuenta  →" else "Iniciar sesión  →",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!esRegistro) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = mantener,
                            onCheckedChange = { mantener = it },
                            colors = CheckboxDefaults.colors(checkedColor = AzulBoton)
                        )
                        Text("Mantener sesión", color = TextoGris, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = AzulBoton,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { /* pendiente */ }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = BordeCampo)
                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (esRegistro) "¿Ya tienes cuenta?" else "¿No tienes cuenta?",
                        color = TextoGris,
                        fontSize = 15.sp
                    )
                    Text(
                        if (esRegistro) "Iniciar sesión" else "Crear cuenta",
                        color = AzulBoton,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { esRegistro = !esRegistro; error = "" }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        // ---------- PIE ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎓", fontSize = 30.sp)
            Text("Tu sala, siempre al alcance", color = TextoGris, fontSize = 14.sp)
            TextButton(onClick = onVolver) {
                Text("← Volver a las salas", color = TextoGris)
            }
        }
    }
}
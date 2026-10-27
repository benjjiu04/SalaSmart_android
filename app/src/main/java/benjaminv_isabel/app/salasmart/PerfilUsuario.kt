package benjaminv_isabel.app.salasmart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColoresAvatar = listOf(
    Color(0xFF2563EB), Color(0xFF7C3AED), Color(0xFF0D9488),
    Color(0xFFEA580C), Color(0xFFDB2777), Color(0xFF0891B2),
    Color(0xFF16A34A), Color(0xFFCA8A04)
)

/** "isabel.rivas@correo.cl" -> "Isabel" */
fun nombreDesdeCorreo(correo: String): String {
    val base = correo.substringBefore("@").substringBefore(".").substringBefore("_")
    return base.replaceFirstChar { it.uppercase() }.ifBlank { "Usuario" }
}

/** Círculo con la inicial del correo y un color propio de cada persona. */
@Composable
fun AvatarUsuario(correo: String, tamano: Dp = 48.dp) {
    val color = ColoresAvatar[(correo.lowercase().hashCode() and Int.MAX_VALUE) % ColoresAvatar.size]
    val inicial = nombreDesdeCorreo(correo).first().toString()
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            inicial, color = Color.White,
            fontSize = (tamano.value * 0.45f).sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/** Perfil completo: avatar + nombre + correo. */
@Composable
fun PerfilUsuario(
    correo: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 48.dp,
    colorNombre: Color = Color(0xFF0B1F4D),
    colorCorreo: Color = Color(0xFF475569)
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AvatarUsuario(correo, tamano)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                nombreDesdeCorreo(correo), color = colorNombre,
                fontSize = 17.sp, fontWeight = FontWeight.Bold
            )
            Text(
                correo, color = colorCorreo, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}
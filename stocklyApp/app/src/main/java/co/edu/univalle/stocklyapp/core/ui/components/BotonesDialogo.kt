package co.edu.univalle.stocklyapp.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

/** Acción afirmativa de un diálogo (verde, color secundario). */
@Composable
fun BotonConfirmar(
    texto: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColoresApp.VerdeBoton,
            contentColor = Color.White,
            disabledContainerColor = ColoresApp.VerdeBoton.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(texto)
        }
    }
}

/** Acción negativa de un diálogo (rojo). */
@Composable
fun BotonCancelar(
    texto: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(contentColor = ColoresApp.Error)
    ) {
        Text(texto)
    }
}

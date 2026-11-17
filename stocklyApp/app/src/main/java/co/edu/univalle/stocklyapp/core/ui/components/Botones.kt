package co.edu.univalle.stocklyapp.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

@Composable
fun BotonVerde(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    colorFondo: Color = ColoresApp.VerdeBoton
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = Color.White,
            disabledContainerColor = colorFondo.copy(alpha = 0.6f),
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
            Text(texto.uppercase())
        }
    }
}

/** Ej: "¿Ya tienes una cuenta? Inicia sesión aquí" donde la segunda parte es el enlace. */
@Composable
fun TextoConEnlace(
    textoNormal: String,
    textoEnlace: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val texto = buildAnnotatedString {
        append("$textoNormal ")
        withLink(
            LinkAnnotation.Clickable(
                tag = textoEnlace,
                styles = TextLinkStyles(style = SpanStyle(color = ColoresApp.AzulEnlace)),
                linkInteractionListener = { onClick() }
            )
        ) {
            append(textoEnlace)
        }
    }
    Text(
        text = texto,
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = ColoresApp.TextoSecundario
    )
}

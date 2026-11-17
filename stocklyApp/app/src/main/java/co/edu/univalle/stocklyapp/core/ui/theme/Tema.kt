package co.edu.univalle.stocklyapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Azul = principal, verde = secundario, rojo = negaciones / errores
private val EsquemaClaro = lightColorScheme(
    primary = ColoresApp.AzulEnlace,
    onPrimary = Color.White,
    primaryContainer = ColoresApp.AzulClaro,
    onPrimaryContainer = ColoresApp.AzulEnlace,
    secondary = ColoresApp.VerdeBoton,
    onSecondary = Color.White,
    error = ColoresApp.Error,
    onError = Color.White,
    background = Color.White,
    onBackground = Color(0xFF202124),
    surface = Color.White,
    onSurface = Color(0xFF202124),
    surfaceVariant = Color(0xFFF1F3F4),
    onSurfaceVariant = ColoresApp.TextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF8F9FA),
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(0xFFF1F3F4),
    outline = Color(0xFF9AA0A6),
    outlineVariant = ColoresApp.Linea
)

@Composable
fun StocklyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaClaro, content = content)
}

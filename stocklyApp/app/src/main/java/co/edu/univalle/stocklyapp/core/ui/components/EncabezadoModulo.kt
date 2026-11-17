package co.edu.univalle.stocklyapp.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun EncabezadoModulo(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    contenidoFinal: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ColoresApp.AzulFondo,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = subtitulo, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            contenidoFinal?.invoke()
        }
    }
}

@Preview(
    showBackground = true,
    name = "Preview - Encabezado Módulo"
)
@Composable
fun EncabezadoModuloPreview() {
    StocklyAppTheme {
        EncabezadoModulo(
            titulo = "Gestión de Inventario",
            subtitulo = "Panel de Propietario",
            icono = Icons.Default.Inventory,
            modifier = Modifier.padding(16.dp)
        )
    }
}

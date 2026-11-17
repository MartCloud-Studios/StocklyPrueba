package co.edu.univalle.stocklyapp.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

/** Pantalla temporal para las pestañas que aún no se construyen. */
@Composable
fun PantallaPendiente(
    titulo: String,
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    Scaffold(
        bottomBar = { BarraNavegacionInferior(rutaActual, onNavegar) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$titulo\nPróximamente",
                textAlign = TextAlign.Center,
                color = ColoresApp.TextoSecundario
            )
        }
    }
}

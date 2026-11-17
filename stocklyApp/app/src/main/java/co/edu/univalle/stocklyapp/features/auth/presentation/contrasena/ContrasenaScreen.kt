package co.edu.univalle.stocklyapp.features.auth.presentation.contrasena

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.core.ui.components.BotonVerde
import co.edu.univalle.stocklyapp.core.ui.components.CampoTexto
import co.edu.univalle.stocklyapp.features.auth.presentation.registro.RegistroScreen
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun ContrasenaScreen(
    onVolver: () -> Unit,
    viewModel: ContrasenaViewModel = viewModel(factory = ContrasenaViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Escribe tu correo y te enviaremos las instrucciones para recuperarla",
            style = MaterialTheme.typography.bodyMedium,
            color = ColoresApp.TextoSecundario,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        CampoTexto(
            valor = state.correo,
            onValorChange = viewModel::onCorreoChange,
            etiqueta = "Correo electrónico",
            error = state.errorCorreo,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { viewModel.enviar() }),
            enabled = !state.cargando
        )

        Spacer(Modifier.height(30.dp))

        state.errorGeneral?.let {
            Text(it, color = ColoresApp.Error, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
        }
        state.mensajeExito?.let {
            Text(it, color = ColoresApp.VerdeBoton, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
        }

        BotonVerde(
            texto = "Enviar instrucciones",
            onClick = viewModel::enviar,
            cargando = state.cargando
        )

        TextButton(onClick = onVolver) {
            Text("Volver a iniciar sesión", color = ColoresApp.AzulEnlace)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Vista previa - Cambiar contraseña"
)
@Composable
private fun ContrasenaScreenPreview() {
    StocklyAppTheme {
        ContrasenaScreen(
            onVolver = {}
        )
    }
}

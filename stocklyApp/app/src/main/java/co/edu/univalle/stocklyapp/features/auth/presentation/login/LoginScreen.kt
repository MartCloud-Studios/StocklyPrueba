package co.edu.univalle.stocklyapp.features.auth.presentation.login

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.core.ui.components.BotonVerde
import co.edu.univalle.stocklyapp.core.ui.components.CampoContrasena
import co.edu.univalle.stocklyapp.core.ui.components.CampoTexto
import co.edu.univalle.stocklyapp.core.ui.components.TextoConEnlace
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onOlvideContrasena: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) onLoginExitoso()
    }

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
            text = "Stockly",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ColoresApp.AzulEnlace
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Inicia sesión",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Para continuar a tu sección según tu tipo de cuenta",
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
            enabled = !state.cargando
        )
        CampoContrasena(
            valor = state.contrasena,
            onValorChange = viewModel::onContrasenaChange,
            etiqueta = "Contraseña",
            error = state.errorContrasena,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { viewModel.iniciarSesion() }),
            enabled = !state.cargando
        )

        Spacer(Modifier.height(16.dp))

        state.errorGeneral?.let {
            Text(
                text = it,
                color = ColoresApp.Error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        BotonVerde(
            texto = "Iniciar sesión",
            onClick = viewModel::iniciarSesion,
            cargando = state.cargando
        )

        Spacer(Modifier.height(24.dp))

        TextoConEnlace(
            textoNormal = "¿No tienes una cuenta?",
            textoEnlace = "Crea una cuenta aquí",
            onClick = onIrARegistro
        )
        TextButton(onClick = onOlvideContrasena) {
            Text("Olvidé mi contraseña", color = ColoresApp.AzulEnlace)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Vista previa - Inicio de sesión"
)
@Composable
private fun LoginScreenPreview() {
    StocklyAppTheme {
        LoginScreen(
            onLoginExitoso = {},
            onIrARegistro = {},
            onOlvideContrasena = {}
        )
    }
}

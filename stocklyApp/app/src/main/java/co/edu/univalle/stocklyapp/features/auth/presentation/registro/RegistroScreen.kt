package co.edu.univalle.stocklyapp.features.auth.presentation.registro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import co.edu.univalle.stocklyapp.features.auth.presentation.login.LoginScreen
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit,
    viewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) onRegistroExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Stockly",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ColoresApp.AzulEnlace
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Regístra tu cuenta",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Crea tu cuenta para gestionar tus ventas e inventarios",
            style = MaterialTheme.typography.bodyMedium,
            color = ColoresApp.TextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = state.nombreUsuario,
            onValorChange = viewModel::onNombreUsuarioChange,
            etiqueta = "Nombre de usuario",
            error = state.errorNombreUsuario,
            enabled = !state.cargando
        )
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
            enabled = !state.cargando
        )
        CampoContrasena(
            valor = state.confirmarContrasena,
            onValorChange = viewModel::onConfirmarContrasenaChange,
            etiqueta = "Confirmar contraseña",
            error = state.errorConfirmarContrasena,
            enabled = !state.cargando
        )
        CampoTexto(
            valor = state.nombreNegocio,
            onValorChange = viewModel::onNombreNegocioChange,
            etiqueta = "Nombre de tu negocio",
            error = state.errorNombreNegocio,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { viewModel.registrar() }),
            enabled = !state.cargando
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = state.aceptaCondiciones,
                onCheckedChange = viewModel::onAceptaCondicionesChange,
                enabled = !state.cargando
            )
            Text(
                text = "Acepto expresamente las condiciones de activación de mi cuenta por medio de la plataforma, para la gestión de mis inventarios, productos y/o servicios ofrecidos en mi negocio",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable(enabled = !state.cargando) {
                        viewModel.onAceptaCondicionesChange(!state.aceptaCondiciones)
                    }
            )
        }
        state.errorCondiciones?.let {
            Text(
                text = it,
                color = ColoresApp.Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        state.errorGeneral?.let {
            Text(
                text = it,
                color = ColoresApp.Error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        BotonVerde(
            texto = "Registrarse",
            onClick = viewModel::registrar,
            cargando = state.cargando
        )

        Spacer(Modifier.height(8.dp))

        TextoConEnlace(
            textoNormal = "¿Ya tienes una cuenta?",
            textoEnlace = "Inicia sesión aquí",
            onClick = onIrALogin
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Vista previa - Registro"
)
@Composable
private fun RegistroScreenPreview() {
    StocklyAppTheme {
        RegistroScreen(
            onRegistroExitoso = {},
            onIrALogin = {}
        )
    }
}

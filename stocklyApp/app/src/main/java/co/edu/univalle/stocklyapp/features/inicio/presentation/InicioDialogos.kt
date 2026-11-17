package co.edu.univalle.stocklyapp.features.inicio.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.core.ui.components.BotonCancelar
import co.edu.univalle.stocklyapp.core.ui.components.BotonConfirmar
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

@Composable
fun DialogosInicio(
    state: InicioUiState,
    viewModel: InicioViewModel,
    onCerrarSesionConfirmado: () -> Unit
) {
    when (val d = state.dialogo) {
        null -> Unit

        is DialogoInicio.RolParaNuevo -> DialogoSeleccionRol(
            correo = d.correo,
            rolInicial = null,
            ocupado = state.ocupado,
            onCancelar = viewModel::cerrarDialogo,
            onAceptar = viewModel::anadirConRol
        )

        is DialogoInicio.CambiarRol -> DialogoSeleccionRol(
            correo = d.usuario.correo,
            rolInicial = d.usuario.rol,
            ocupado = state.ocupado,
            onCancelar = viewModel::cerrarDialogo,
            onAceptar = viewModel::cambiarRol
        )

        DialogoInicio.SinRolesEnOtrosNegocios -> AlertDialog(
            onDismissRequest = viewModel::cerrarDialogo,
            title = { Text("Cambiar de rol y/o negocio") },
            text = { Text("Actualmente no tienes roles asignados en otros negocios") },
            confirmButton = {
                TextButton(onClick = viewModel::cerrarDialogo) { Text("Entendido") }
            }
        )

        is DialogoInicio.OtrosRoles -> AlertDialog(
            onDismissRequest = viewModel::cerrarDialogo,
            title = { Text("Cambiar de rol y/o negocio") },
            text = {
                Column {
                    d.items.forEachIndexed { i, item ->
                        if (i > 0) HorizontalDivider()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.seleccionarNegocio(item) }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(item.nombre, fontWeight = FontWeight.Bold)
                            Text(
                                "Rol: ${item.rol.lowercase().replace('_', ' ')}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresApp.TextoSecundario
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { BotonCancelar("Cancelar", onClick = viewModel::cerrarDialogo) }
        )

        DialogoInicio.ConfirmarCerrarSesion -> AlertDialog(
            onDismissRequest = viewModel::cerrarDialogo,
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrás que iniciar sesión de nuevo para entrar a tu negocio") },
            confirmButton = { BotonConfirmar("Aceptar", onClick = onCerrarSesionConfirmado) },
            dismissButton = { BotonCancelar("Cancelar", onClick = viewModel::cerrarDialogo) }
        )
    }
}

/** "Seleccionar rol para: correo" con la lista de roles y su descripción (la "infoventana"). */
@Composable
private fun DialogoSeleccionRol(
    correo: String,
    rolInicial: Rol?,
    ocupado: Boolean,
    onCancelar: () -> Unit,
    onAceptar: (Rol) -> Unit
) {
    var seleccionado by remember { mutableStateOf(rolInicial) }

    AlertDialog(
        onDismissRequest = { if (!ocupado) onCancelar() },
        title = { Text("Seleccionar rol para:") },
        text = {
            Column {
                Text(correo, color = ColoresApp.AzulEnlace, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Rol.entries.forEach { rol ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = seleccionado == rol,
                                enabled = !ocupado,
                                role = Role.RadioButton,
                                onClick = { seleccionado = rol }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(selected = seleccionado == rol, onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(rol.etiqueta, fontWeight = FontWeight.Bold)
                            Text(
                                rol.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresApp.TextoSecundario
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Aceptar",
                onClick = { seleccionado?.let(onAceptar) },
                enabled = seleccionado != null && !ocupado,
                cargando = ocupado
            )
        },
        dismissButton = {
            BotonCancelar("Cancelar", onClick = onCancelar, enabled = !ocupado)
        }
    )
}

package co.edu.univalle.stocklyapp.features.inventario.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Etiqueta
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.UnidadMedida
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.core.ui.components.BotonCancelar
import co.edu.univalle.stocklyapp.core.ui.components.BotonConfirmar
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

@Composable
fun InventarioScreen(
    onNavegar: (String) -> Unit,
    onIrACompras: () -> Unit,
    onIrAVentas: () -> Unit,
    viewModel: InventarioViewModel = viewModel(factory = InventarioViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        bottomBar = { BarraNavegacionInferior(Routes.INVENTARIO, onNavegar) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.esAlmacenista) {
                FloatingActionButton(
                    onClick = viewModel::abrirCrearProducto,
                    containerColor = ColoresApp.VerdeBoton,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Header Superior
            EncabezadoModulo(
                titulo = "Catálogo e inventario",
                subtitulo = state.nombreNegocio,
                icono = Icons.Default.Category,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                contenidoFinal = {
                    if (state.esAlmacenista) {
                        OutlinedButton(
                            onClick = viewModel::abrirGestionarEtiquetas,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.Category, contentDescription = "Categorías", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            )

            // Buscador y Filtro Bajo Stock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.busquedaTexto,
                    onValueChange = viewModel::onBusquedaChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar producto...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.width(8.dp))

                FilterChip(
                    selected = state.soloBajoStock,
                    onClick = viewModel::toggleSoloBajoStock,
                    label = { Text("Bajo Stock") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (state.soloBajoStock) ColoresApp.Error else ColoresApp.TextoSecundario
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ColoresApp.Error.copy(alpha = 0.15f),
                        selectedLabelColor = ColoresApp.Error
                    )
                )
            }

            if (state.esAlmacenista) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilterChip(
                        selected = state.mostrarInactivos,
                        onClick = viewModel::toggleMostrarInactivos,
                        label = { Text("Incluir inactivos") }
                    )
                }
            }

            // Chips de Etiquetas / Categorías (Padre e Hijas)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.etiquetaSeleccionadaId == null,
                        onClick = { viewModel.seleccionarEtiqueta(null) },
                        label = { Text("Todas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColoresApp.AzulEnlace,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(state.etiquetas.filter { it.activo }) { et ->
                    val esHija = et.idPadre != null
                    FilterChip(
                        selected = state.etiquetaSeleccionadaId == et.id,
                        onClick = { viewModel.seleccionarEtiqueta(et.id) },
                        label = {
                            Text(
                                if (esHija) "  ↳ ${et.nombre}" else et.nombre,
                                fontWeight = if (esHija) FontWeight.Normal else FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColoresApp.AzulEnlace,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Lista de Productos
            val productos = state.productosFiltrados
            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron productos en el inventario",
                        color = ColoresApp.TextoSecundario,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos, key = { it.id }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            esAlmacenista = state.esAlmacenista,
                            esVendedor = state.esVendedor,
                            esPropietario = state.esPropietario,
                            onEditar = { viewModel.abrirEditarProducto(producto) },
                            onCambiarEstado = { viewModel.cambiarEstadoProducto(producto) },
                            onReabastecer = { viewModel.abrirReabastecerRapido(producto) },
                            onAjustar = { viewModel.abrirAjuste(producto) },
                            onVender = onIrAVentas
                        )
                    }
                }
            }
        }
    }

    // Diálogos de Gestión
    DialogosInventario(
        state = state,
        viewModel = viewModel
    )
}

@Composable
private fun TarjetaProducto(
    producto: Producto,
    esAlmacenista: Boolean,
    esVendedor: Boolean,
    esPropietario: Boolean,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit,
    onReabastecer: () -> Unit,
    onAjustar: () -> Unit,
    onVender: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    producto.etiqueta?.let { et ->
                        Text(
                            text = if (et.nombrePadre != null) "${et.nombrePadre} > ${et.nombre}" else et.nombre,
                            style = MaterialTheme.typography.labelSmall,
                            color = ColoresApp.AzulEnlace
                        )
                    }
                }

                if (esAlmacenista) {
                    Row {
                        IconButton(onClick = onEditar, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ColoresApp.TextoSecundario)
                        }
                        IconButton(onClick = onCambiarEstado, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (producto.activo) Icons.Default.DeleteOutline else Icons.Default.CheckCircle,
                                contentDescription = if (producto.activo) "Desactivar producto" else "Reactivar producto",
                                tint = if (producto.activo) ColoresApp.Error else ColoresApp.VerdeBoton
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Información de Stock con alerta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (producto.bajoStock) ColoresApp.Error else ColoresApp.VerdeBoton)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Disponible: ${producto.cantidadDisponible} ${producto.unidadMedida.abreviatura}",
                        fontWeight = FontWeight.SemiBold,
                        color = if (producto.bajoStock) ColoresApp.Error else Color.Unspecified
                    )
                }

                Text(
                    text = "Mín: ${producto.cantidadMinima}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresApp.TextoSecundario
                )
            }

            Spacer(Modifier.height(8.dp))

            // Precios según Rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Costo: $${String.format("%,.2f", producto.costoActual)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColoresApp.TextoSecundario
                )
                Text(
                    text = "Precio Venta: $${String.format("%,.0f", producto.precioVenta)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColoresApp.VerdeFondoOscuro
                )
            }

            Spacer(Modifier.height(12.dp))

            // Acciones Rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (esAlmacenista && producto.activo) {
                    OutlinedButton(
                        onClick = onReabastecer,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reabastecer", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onAjustar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ajustar", fontSize = 12.sp)
                    }
                }

                if (esVendedor && producto.activo) {
                    Button(
                        onClick = onVender,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.VerdeBoton)
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Vender", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogosInventario(
    state: InventarioUiState,
    viewModel: InventarioViewModel
) {
    when (val d = state.dialogo) {
        null -> Unit

        DialogoInventario.CrearProducto -> DialogoFormularioProducto(
            productoExistente = null,
            etiquetas = state.etiquetas,
            unidades = state.unidadesMedida,
            proveedores = state.proveedores,
            onCancelar = viewModel::cerrarDialogo,
            onGuardar = viewModel::guardarProducto
        )

        is DialogoInventario.EditarProducto -> DialogoFormularioProducto(
            productoExistente = d.producto,
            etiquetas = state.etiquetas,
            unidades = state.unidadesMedida,
            proveedores = state.proveedores,
            onCancelar = viewModel::cerrarDialogo,
            onGuardar = viewModel::guardarProducto
        )

        is DialogoInventario.ReabastecerRapido -> DialogoReabastecimientoRapido(
            producto = d.producto,
            onCancelar = viewModel::cerrarDialogo,
            onConfirmar = { cantidad, motivo ->
                viewModel.procesarReabastecimientoRapido(d.producto.id, cantidad, motivo)
            }
        )

        is DialogoInventario.AjustarStock -> DialogoAjusteInventario(
            producto = d.producto,
            onCancelar = viewModel::cerrarDialogo,
            onConfirmar = { cantidadReal, motivo ->
                viewModel.registrarAjuste(d.producto.id, cantidadReal, motivo)
            }
        )

        DialogoInventario.GestionarEtiquetas -> DialogoGestionEtiquetas(
            onCancelar = viewModel::cerrarDialogo,
            onCrear = viewModel::crearEtiqueta
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFormularioProducto(
    productoExistente: Producto?,
    etiquetas: List<Etiqueta>,
    unidades: List<UnidadMedida>,
    proveedores: List<Proveedor>,
    onCancelar: () -> Unit,
    onGuardar: (Long?, String, String, Long?, Long?, Long, Float, Double, Double, String) -> Unit
) {
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var codigo by remember { mutableStateOf(productoExistente?.codigoBarras ?: "") }
    var etiquetaId by remember { mutableStateOf(productoExistente?.etiqueta?.id) }
    var proveedorId by remember { mutableStateOf(productoExistente?.proveedorId) }
    var unidadId by remember {
        mutableStateOf(
            productoExistente?.unidadMedida?.id
                ?: unidades.firstOrNull { !it.esPersonalizada }?.id
                ?: unidades.firstOrNull()?.id
                ?: 1L
        )
    }
    var cantidadMinima by remember { mutableStateOf(productoExistente?.cantidadMinima?.toString() ?: "5") }
    var costo by remember { mutableStateOf(productoExistente?.costoActual?.toString() ?: "0") }
    var precio by remember { mutableStateOf(productoExistente?.precioVenta?.toString() ?: "0") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion.orEmpty()) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var proveedorExpandido by remember { mutableStateOf(false) }
    var unidadExpandida by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (productoExistente == null) "Nuevo Producto" else "Editar Producto") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código de Barras (Opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = categoriaExpandida,
                    onExpandedChange = { categoriaExpandida = it }
                ) {
                    OutlinedTextField(
                        value = etiquetas.firstOrNull { it.id == etiquetaId }?.nombre ?: "Sin categoría",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría (opcional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoriaExpandida) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaExpandida,
                        onDismissRequest = { categoriaExpandida = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin categoría") },
                            onClick = {
                                etiquetaId = null
                                categoriaExpandida = false
                            }
                        )
                        etiquetas.filter { it.activo || it.id == etiquetaId }.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    etiquetaId = categoria.id
                                    categoriaExpandida = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = proveedorExpandido,
                    onExpandedChange = { proveedorExpandido = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = proveedores.firstOrNull { it.id == proveedorId }?.nombre ?: "Sin proveedor",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proveedor (opcional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(proveedorExpandido) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = proveedorExpandido,
                        onDismissRequest = { proveedorExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin proveedor") },
                            onClick = {
                                proveedorId = null
                                proveedorExpandido = false
                            }
                        )
                        proveedores.filter { it.activo }.forEach { proveedor ->
                            DropdownMenuItem(
                                text = { Text(proveedor.nombre) },
                                onClick = {
                                    proveedorId = proveedor.id
                                    proveedorExpandido = false
                                }
                            )
                        }
                    }
                }

                if (productoExistente == null) {
                    ExposedDropdownMenuBox(
                        expanded = unidadExpandida,
                        onExpandedChange = { unidadExpandida = it }
                    ) {
                        OutlinedTextField(
                            value = unidades.firstOrNull { it.id == unidadId }?.nombre.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad de medida") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unidadExpandida) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = unidadExpandida,
                            onDismissRequest = { unidadExpandida = false }
                        ) {
                            unidades.forEach { unidad ->
                                DropdownMenuItem(
                                    text = { Text(unidad.nombre) },
                                    onClick = {
                                        unidadId = unidad.id
                                        unidadExpandida = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = productoExistente.unidadMedida.nombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad de medida (no editable)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = costo,
                        onValueChange = { costo = it },
                        label = { Text("Costo ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio Venta ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = cantidadMinima,
                    onValueChange = { cantidadMinima = it },
                    label = { Text("Stock Mínimo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Guardar",
                onClick = {
                    onGuardar(
                        productoExistente?.id,
                        nombre,
                        codigo,
                        etiquetaId,
                        proveedorId,
                        unidadId,
                        cantidadMinima.toFloatOrNull() ?: -1f,
                        costo.toDoubleOrNull() ?: -1.0,
                        precio.toDoubleOrNull() ?: -1.0,
                        descripcion
                    )
                }
            )
        },
        dismissButton = { BotonCancelar("Cancelar", onClick = onCancelar) }
    )
}

@Composable
private fun DialogoReabastecimientoRapido(
    producto: Producto,
    onCancelar: () -> Unit,
    onConfirmar: (Float, String?) -> Unit
) {
    var cantidad by remember { mutableStateOf("10") }
    var motivo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Reabastecer: ${producto.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stock actual: ${producto.cantidadDisponible} ${producto.unidadMedida.abreviatura}")
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad de entrada") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Comprar",
                onClick = {
                    onConfirmar(cantidad.toFloatOrNull() ?: 0f, motivo.ifBlank { null })
                }
            )
        },
        dismissButton = { BotonCancelar("Cancelar", onClick = onCancelar) }
    )
}

@Composable
private fun DialogoAjusteInventario(
    producto: Producto,
    onCancelar: () -> Unit,
    onConfirmar: (Float, String?) -> Unit
) {
    var cantidadReal by remember { mutableStateOf(producto.cantidadDisponible.toString()) }
    var motivo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Ajustar: ${producto.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stock en sistema: ${producto.cantidadDisponible} ${producto.unidadMedida.abreviatura}")
                OutlinedTextField(
                    value = cantidadReal,
                    onValueChange = { cantidadReal = it },
                    label = { Text("Cantidad real contada") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Guardar ajuste",
                onClick = {
                    onConfirmar(cantidadReal.toFloatOrNull() ?: -1f, motivo.ifBlank { null })
                }
            )
        },
        dismissButton = { BotonCancelar("Cancelar", onClick = onCancelar) }
    )
}

@Composable
private fun DialogoGestionEtiquetas(
    onCancelar: () -> Unit,
    onCrear: (String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Gestión de Etiquetas / Categorías") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Crear una categoría:", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (Ej: Bebidas, Snacks)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Crear",
                onClick = {
                    onCrear(nombre)
                    nombre = ""
                }
            )
        },
        dismissButton = { BotonCancelar("Cerrar", onClick = onCancelar) }
    )
}


@Preview(
    name = "Inventario - Diseño",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun InventarioScreenPreview() {
    MaterialTheme {
        EncabezadoModulo("Inventario", "Mi negocio", Icons.Default.Category)
    }
}
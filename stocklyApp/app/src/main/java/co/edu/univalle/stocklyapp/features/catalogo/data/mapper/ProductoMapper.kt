package co.edu.univalle.stocklyapp.features.catalogo.data.mapper

import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProductoDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.StockDto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Etiqueta
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.UnidadMedida

fun ProductoDto.toProducto(stock: StockDto? = null) = Producto(
    id = id,
    negocioId = negocioId,
    nombre = nombre,
    codigoBarras = codigoBarras,
    etiqueta = categoriaId?.let { Etiqueta(it, categoriaNombre.orEmpty()) },
    unidadMedida = UnidadMedida(
        id = unidadMedidaId,
        nombre = unidadMedidaNombre,
        abreviatura = unidadMedidaNombre,
        esPersonalizada = !unidadBase
    ),
    cantidadDisponible = (stock?.cantidad ?: 0.0).toFloat(),
    cantidadMinima = stock?.stockMinimo?.toFloat() ?: stockMinimo.toFloat(),
    costoActual = precioCosto,
    precioVenta = precioVenta,
    fechaIngreso = createdAt,
    descripcion = descripcion,
    proveedorId = proveedorId,
    activo = activo
)

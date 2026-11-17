package co.edu.univalle.stocklyapp.features.inicio.data.repository

import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.network.safeApiCall
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.inicio.domain.model.AlertaStockBajo
import co.edu.univalle.stocklyapp.features.inicio.domain.model.EstadisticasInicio
import co.edu.univalle.stocklyapp.features.inicio.domain.model.ProductoTop
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object EstadisticasInicioRepository {
    suspend fun cargar(negocioId: Long): Resource<EstadisticasInicio> = safeApiCall {
        val stockBajo = AppContainer.inventarioApi.stock(negocioId, soloBajos = true)
        val productos = AppContainer.catalogoApi.productos(negocioId, soloActivos = true)
            .associateBy { it.id }
        val ventas = AppContainer.ventasApi.ventas(negocioId, page = 0, size = 20).content
        val detalles = coroutineScope {
            ventas.map { venta ->
                async { AppContainer.ventasApi.venta(negocioId, venta.id) }
            }.awaitAll()
        }
        val agrupado = mutableMapOf<Long, VentasProducto>()
        detalles.flatMap { it.lineas }.forEach { linea ->
            val actual = agrupado[linea.productoId] ?: VentasProducto(
                nombre = linea.productoNombre,
                unidad = productos[linea.productoId]?.unidadMedidaNombre ?: "Unidad"
            )
            actual.cantidad += linea.cantidad
            actual.total += linea.subtotal
            agrupado[linea.productoId] = actual
        }
        val masVendidos = agrupado.values
            .sortedByDescending { it.cantidad }
            .take(5)
            .map { ProductoTop(it.nombre, it.cantidad.toFloat(), it.total, it.unidad) }
        val menosVendidos = agrupado.values
            .sortedBy { it.cantidad }
            .take(5)
            .map { ProductoTop(it.nombre, it.cantidad.toFloat(), it.total, it.unidad) }
        EstadisticasInicio(
            alertasBajoStock = stockBajo.map {
                AlertaStockBajo(it.nombre, it.cantidad.toFloat(), it.stockMinimo.toFloat(), it.unidadMedida)
            },
            topMasVendidos = masVendidos,
            topMenosVendidos = menosVendidos,
            evolucionStock = emptyList()
        )
    }

    private data class VentasProducto(
        val nombre: String,
        val unidad: String,
        var cantidad: Double = 0.0,
        var total: Double = 0.0
    )
}

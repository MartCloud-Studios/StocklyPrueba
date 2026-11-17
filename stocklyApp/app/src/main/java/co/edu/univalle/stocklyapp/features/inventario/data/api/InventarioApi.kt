package co.edu.univalle.stocklyapp.features.inventario.data.api

import co.edu.univalle.stocklyapp.features.inventario.data.dto.AjusteRequestDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.EntradaRequestDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.MovimientoDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.PaginaDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.StockDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InventarioApi {
    @GET("api/negocios/{negocioId}/inventario/stock")
    suspend fun stock(
        @Path("negocioId") negocioId: Long,
        @Query("soloBajos") soloBajos: Boolean = false
    ): List<StockDto>

    @GET("api/negocios/{negocioId}/inventario/movimientos")
    suspend fun movimientos(
        @Path("negocioId") negocioId: Long,
        @Query("productoId") productoId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): PaginaDto<MovimientoDto>

    @POST("api/negocios/{negocioId}/inventario/stock-inicial")
    suspend fun registrarStockInicial(
        @Path("negocioId") negocioId: Long,
        @Body request: EntradaRequestDto
    ): MovimientoDto

    @POST("api/negocios/{negocioId}/inventario/entradas")
    suspend fun registrarEntrada(
        @Path("negocioId") negocioId: Long,
        @Body request: EntradaRequestDto
    ): MovimientoDto

    @POST("api/negocios/{negocioId}/inventario/ajustes")
    suspend fun registrarAjuste(
        @Path("negocioId") negocioId: Long,
        @Body request: AjusteRequestDto
    ): MovimientoDto
}

package co.edu.univalle.stocklyapp.features.compras.data.api

import co.edu.univalle.stocklyapp.features.compras.data.dto.CompraDto
import co.edu.univalle.stocklyapp.features.compras.data.dto.CompraResumenDto
import co.edu.univalle.stocklyapp.features.compras.data.dto.CrearCompraDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.PaginaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ComprasApi {
    @GET("api/negocios/{negocioId}/compras")
    suspend fun compras(@Path("negocioId") negocioId: Long): PaginaDto<CompraResumenDto>

    @GET("api/negocios/{negocioId}/compras/{compraId}")
    suspend fun compra(
        @Path("negocioId") negocioId: Long,
        @Path("compraId") compraId: Long
    ): CompraDto

    @POST("api/negocios/{negocioId}/compras")
    suspend fun registrar(
        @Path("negocioId") negocioId: Long,
        @Body request: CrearCompraDto
    ): CompraDto
}

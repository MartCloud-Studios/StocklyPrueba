package co.edu.univalle.stocklyapp.features.ventas.data.api

import co.edu.univalle.stocklyapp.features.inventario.data.dto.PaginaDto
import co.edu.univalle.stocklyapp.features.ventas.data.dto.CrearVentaDto
import co.edu.univalle.stocklyapp.features.ventas.data.dto.VentaDto
import co.edu.univalle.stocklyapp.features.ventas.data.dto.VentaResumenDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VentasApi {
    @GET("api/negocios/{negocioId}/ventas")
    suspend fun ventas(
        @Path("negocioId") negocioId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PaginaDto<VentaResumenDto>

    @GET("api/negocios/{negocioId}/ventas/{ventaId}")
    suspend fun venta(
        @Path("negocioId") negocioId: Long,
        @Path("ventaId") ventaId: Long
    ): VentaDto

    @POST("api/negocios/{negocioId}/ventas")
    suspend fun registrar(
        @Path("negocioId") negocioId: Long,
        @Body request: CrearVentaDto
    ): VentaDto
}

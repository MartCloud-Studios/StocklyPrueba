package co.edu.univalle.stocklyapp.features.catalogo.data.api

import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ActualizarProductoRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.CategoriaDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.CategoriaRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.CrearProductoRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.NombreRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProductoDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProveedorDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProveedorRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.UnidadMedidaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogoApi {
    @GET("api/negocios/{negocioId}/catalogo/categorias")
    suspend fun categorias(@Path("negocioId") negocioId: Long): List<CategoriaDto>

    @POST("api/negocios/{negocioId}/catalogo/categorias")
    suspend fun crearCategoria(
        @Path("negocioId") negocioId: Long,
        @Body request: CategoriaRequestDto
    ): CategoriaDto

    @GET("api/negocios/{negocioId}/catalogo/unidades-medida")
    suspend fun unidades(@Path("negocioId") negocioId: Long): List<UnidadMedidaDto>

    @GET("api/negocios/{negocioId}/catalogo/proveedores")
    suspend fun proveedores(@Path("negocioId") negocioId: Long): List<ProveedorDto>

    @POST("api/negocios/{negocioId}/catalogo/proveedores")
    suspend fun crearProveedor(
        @Path("negocioId") negocioId: Long,
        @Body request: ProveedorRequestDto
    ): ProveedorDto

    @GET("api/negocios/{negocioId}/catalogo/productos")
    suspend fun productos(
        @Path("negocioId") negocioId: Long,
        @Query("soloActivos") soloActivos: Boolean = false
    ): List<ProductoDto>

    @POST("api/negocios/{negocioId}/catalogo/productos")
    suspend fun crearProducto(
        @Path("negocioId") negocioId: Long,
        @Body request: CrearProductoRequestDto
    ): ProductoDto

    @PUT("api/negocios/{negocioId}/catalogo/productos/{productoId}")
    suspend fun actualizarProducto(
        @Path("negocioId") negocioId: Long,
        @Path("productoId") productoId: Long,
        @Body request: ActualizarProductoRequestDto
    ): ProductoDto

    @PATCH("api/negocios/{negocioId}/catalogo/productos/{productoId}/desactivar")
    suspend fun desactivarProducto(
        @Path("negocioId") negocioId: Long,
        @Path("productoId") productoId: Long
    ): ProductoDto

    @PATCH("api/negocios/{negocioId}/catalogo/productos/{productoId}/reactivar")
    suspend fun reactivarProducto(
        @Path("negocioId") negocioId: Long,
        @Path("productoId") productoId: Long
    ): ProductoDto
}

package co.edu.univalle.stocklyapp.features.negocio.data.api

import co.edu.univalle.stocklyapp.features.negocio.data.dto.AnadirUsuarioRequestDto
import co.edu.univalle.stocklyapp.features.negocio.data.dto.CambiarRolRequestDto
import co.edu.univalle.stocklyapp.features.negocio.data.dto.UsuarioNegocioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NegocioApi {

    @GET("api/negocios/{negocioId}/usuarios")
    suspend fun obtenerUsuarios(@Path("negocioId") negocioId: Long): List<UsuarioNegocioDto>

    @POST("api/negocios/{negocioId}/usuarios")
    suspend fun anadirUsuario(
        @Path("negocioId") negocioId: Long,
        @Body body: AnadirUsuarioRequestDto
    ): UsuarioNegocioDto

    @PUT("api/negocios/{negocioId}/usuarios/{usuarioId}/rol")
    suspend fun cambiarRol(
        @Path("negocioId") negocioId: Long,
        @Path("usuarioId") usuarioId: Long,
        @Body body: CambiarRolRequestDto
    ): UsuarioNegocioDto
}

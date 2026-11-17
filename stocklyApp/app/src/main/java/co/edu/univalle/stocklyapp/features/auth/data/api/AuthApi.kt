package co.edu.univalle.stocklyapp.features.auth.data.api

import co.edu.univalle.stocklyapp.features.auth.data.dto.LoginRequestDto
import co.edu.univalle.stocklyapp.features.auth.data.dto.LoginResponseDto
import co.edu.univalle.stocklyapp.features.auth.data.dto.RegistroRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/registro")
    suspend fun registrar(@Body body: RegistroRequestDto): LoginResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto
}

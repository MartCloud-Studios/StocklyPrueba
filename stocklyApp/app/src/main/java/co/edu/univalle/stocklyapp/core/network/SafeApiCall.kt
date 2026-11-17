package co.edu.univalle.stocklyapp.core.network

import co.edu.univalle.stocklyapp.core.util.Resource
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/** Ejecuta una llamada de Retrofit y convierte errores en Resource.Error con mensaje en español. */
suspend fun <T> safeApiCall(
    mensajePorCodigo: (Int) -> String? = { null },
    block: suspend () -> T
): Resource<T> = try {
    Resource.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    val codigo = e.code()
    Resource.Error(
        mensajePorCodigo(codigo) ?: when (codigo) {
            401 -> "Tu sesión expiró, inicia sesión de nuevo"
            403 -> "No tienes permisos para realizar esta acción"
            else -> "Error del servidor ($codigo)"
        }
    )
} catch (e: IOException) {
    Resource.Error("No se pudo conectar con el servidor")
} catch (e: Exception) {
    Resource.Error("Ocurrió un error inesperado")
}

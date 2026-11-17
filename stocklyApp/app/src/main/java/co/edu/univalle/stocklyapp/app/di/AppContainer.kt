package co.edu.univalle.stocklyapp.app.di

import co.edu.univalle.stocklyapp.core.network.NetworkModule
import co.edu.univalle.stocklyapp.features.catalogo.data.api.CatalogoApi
import co.edu.univalle.stocklyapp.features.compras.data.api.ComprasApi
import co.edu.univalle.stocklyapp.features.auth.data.api.AuthApi
import co.edu.univalle.stocklyapp.features.auth.data.repository.AuthRepositoryImpl
import co.edu.univalle.stocklyapp.features.inventario.data.api.InventarioApi
import co.edu.univalle.stocklyapp.features.negocio.data.api.NegocioApi
import co.edu.univalle.stocklyapp.features.negocio.data.repository.NegocioRepositoryImpl
import co.edu.univalle.stocklyapp.features.ventas.data.api.VentasApi
import co.edu.univalle.stocklyapp.features.auth.domain.repository.AuthRepository
import co.edu.univalle.stocklyapp.features.negocio.domain.repository.NegocioRepository

/** Inyección de dependencias manual (sin Hilt) para mantenerlo simple. */
object AppContainer {
    val catalogoApi: CatalogoApi by lazy {
        NetworkModule.retrofit.create(CatalogoApi::class.java)
    }

    val inventarioApi: InventarioApi by lazy {
        NetworkModule.retrofit.create(InventarioApi::class.java)
    }

    val comprasApi: ComprasApi by lazy {
        NetworkModule.retrofit.create(ComprasApi::class.java)
    }

    val ventasApi: VentasApi by lazy {
        NetworkModule.retrofit.create(VentasApi::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(NetworkModule.retrofit.create(AuthApi::class.java))
    }

    val negocioRepository: NegocioRepository by lazy {
        NegocioRepositoryImpl(NetworkModule.retrofit.create(NegocioApi::class.java))
    }
}

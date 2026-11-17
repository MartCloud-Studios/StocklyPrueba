package com.stockly.stocklybackend.catalogo.service;

import com.stockly.stocklybackend.acceso.entity.Negocio;
import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.entity.Rol;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.catalogo.dto.CategoriaRequest;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import com.stockly.stocklybackend.catalogo.entity.Proveedor;
import com.stockly.stocklybackend.catalogo.entity.UnidadMedida;
import com.stockly.stocklybackend.catalogo.repository.CategoriaRepository;
import com.stockly.stocklybackend.catalogo.repository.ProductoRepository;
import com.stockly.stocklybackend.catalogo.repository.ProveedorRepository;
import com.stockly.stocklybackend.catalogo.repository.UnidadMedidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class CatalogoServiceTest {
    private final CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
    private final UnidadMedidaRepository unidadMedidaRepository =
            mock(UnidadMedidaRepository.class);
    private final ProductoRepository productoRepository = mock(ProductoRepository.class);
    private final ProveedorRepository proveedorRepository = mock(ProveedorRepository.class);
    private final NegocioUsuarioRepository negocioUsuarioRepository =
            mock(NegocioUsuarioRepository.class);
    private final CatalogoService service = new CatalogoService(categoriaRepository,
            unidadMedidaRepository, productoRepository, proveedorRepository,
            negocioUsuarioRepository);

    @BeforeEach
    void limpiarMocks() {
        org.mockito.Mockito.reset(categoriaRepository, unidadMedidaRepository,
                productoRepository, proveedorRepository, negocioUsuarioRepository);
    }

    @Test
    void unidadBaseSoloAceptaCantidadesEnterasMayoresACero() {
        UnidadMedida unidad = new UnidadMedida();
        Producto producto = producto(unidad);
        when(productoRepository.findByIdAndNegocioIdAndActivoTrue(1L, 7L))
                .thenReturn(Optional.of(producto));

        assertDoesNotThrow(() -> service.validarCantidad(7L, 1L, new BigDecimal("4.000")));
        ResponseStatusException decimal = assertThrows(ResponseStatusException.class,
                () -> service.validarCantidad(7L, 1L, new BigDecimal("1.5")));
        ResponseStatusException cero = assertThrows(ResponseStatusException.class,
                () -> service.validarCantidad(7L, 1L, BigDecimal.ZERO));

        assertEquals(BAD_REQUEST, decimal.getStatusCode());
        assertEquals(BAD_REQUEST, cero.getStatusCode());
    }

    @Test
    void unidadPropiaAdmiteHastaTresDecimales() {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNegocio(new Negocio());
        Producto producto = producto(unidad);
        when(productoRepository.findByIdAndNegocioIdAndActivoTrue(1L, 7L))
                .thenReturn(Optional.of(producto));

        assertDoesNotThrow(() -> service.validarCantidad(7L, 1L, new BigDecimal("2.345")));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.validarCantidad(7L, 1L, new BigDecimal("2.3451")));

        assertEquals(BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void vendedorPuedeLeerPeroNoPuedeCrearCategorias() {
        NegocioUsuario miembro = miembro(NombreRol.VENDEDOR);
        when(negocioUsuarioRepository.findByUsuarioIdAndNegocioId(4L, 7L))
                .thenReturn(Optional.of(miembro));

        assertDoesNotThrow(() -> service.listarCategorias(7L, 4L));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.crearCategoria(7L, 4L, new CategoriaRequest("Abarrotes")));

        assertEquals(FORBIDDEN, error.getStatusCode());
    }

    @Test
    void obtenerProveedorActivoRechazaProveedoresDesactivados() {
        Proveedor proveedor = new Proveedor();
        proveedor.setActivo(false);
        when(proveedorRepository.findByIdAndNegocioId(3L, 7L))
                .thenReturn(Optional.of(proveedor));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.obtenerProveedorActivo(7L, 3L));

        assertEquals(BAD_REQUEST, error.getStatusCode());
    }

    private Producto producto(UnidadMedida unidad) {
        Producto producto = new Producto();
        producto.setUnidadMedida(unidad);
        return producto;
    }

    private NegocioUsuario miembro(NombreRol nombreRol) {
        Negocio negocio = new Negocio();
        negocio.setId(7L);
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        NegocioUsuario miembro = new NegocioUsuario();
        miembro.setNegocio(negocio);
        miembro.setRol(rol);
        miembro.setActivo(true);
        return miembro;
    }
}

package com.stockly.stocklybackend.catalogo.controller;

import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.catalogo.dto.ActualizarProductoRequest;
import com.stockly.stocklybackend.catalogo.dto.CategoriaRequest;
import com.stockly.stocklybackend.catalogo.dto.CategoriaResponse;
import com.stockly.stocklybackend.catalogo.dto.CrearProductoRequest;
import com.stockly.stocklybackend.catalogo.dto.ProductoResponse;
import com.stockly.stocklybackend.catalogo.dto.ProveedorRequest;
import com.stockly.stocklybackend.catalogo.dto.ProveedorResponse;
import com.stockly.stocklybackend.catalogo.dto.UnidadMedidaRequest;
import com.stockly.stocklybackend.catalogo.dto.UnidadMedidaResponse;
import com.stockly.stocklybackend.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios/{negocioId}/catalogo")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Operation(summary = "Listar categorías", tags = "Categorías")
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(catalogoService.listarCategorias(negocioId, usuario.id()));
    }

    @Operation(
            summary = "Crear categoría",
            tags = "Categorías",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Bebidas"
                                    }
                                    """))))
    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoriaResponse> crearCategoria(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.crearCategoria(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Actualizar categoría",
            tags = "Categorías",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Bebidas frías"
                                    }
                                    """))))
    @PutMapping("/categorias/{categoriaId}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(
            @PathVariable Long negocioId,
            @PathVariable Long categoriaId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(catalogoService.actualizarCategoria(
                negocioId, usuario.id(), categoriaId, request));
    }

    @Operation(summary = "Desactivar categoría", tags = "Categorías")
    @PatchMapping("/categorias/{categoriaId}/desactivar")
    public ResponseEntity<CategoriaResponse> desactivarCategoria(
            @PathVariable Long negocioId, @PathVariable Long categoriaId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.desactivarCategoria(negocioId, usuario.id(), categoriaId));
    }

    @Operation(summary = "Reactivar categoría", tags = "Categorías")
    @PatchMapping("/categorias/{categoriaId}/reactivar")
    public ResponseEntity<CategoriaResponse> reactivarCategoria(
            @PathVariable Long negocioId, @PathVariable Long categoriaId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.reactivarCategoria(negocioId, usuario.id(), categoriaId));
    }

    @Operation(summary = "Listar unidades de medida", tags = "Unidades de medida")
    @GetMapping("/unidades-medida")
    public ResponseEntity<List<UnidadMedidaResponse>> listarUnidades(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(catalogoService.listarUnidades(negocioId, usuario.id()));
    }

    @Operation(
            summary = "Crear unidad de medida",
            tags = "Unidades de medida",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Kilogramo"
                                    }
                                    """))))
    @PostMapping("/unidades-medida")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UnidadMedidaResponse> crearUnidad(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody UnidadMedidaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.crearUnidad(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Renombrar unidad de medida",
            tags = "Unidades de medida",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Kilogramo"
                                    }
                                    """))))
    @PutMapping("/unidades-medida/{unidadId}")
    public ResponseEntity<UnidadMedidaResponse> renombrarUnidad(
            @PathVariable Long negocioId, @PathVariable Long unidadId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody UnidadMedidaRequest request) {
        return ResponseEntity.ok(catalogoService.renombrarUnidad(
                negocioId, usuario.id(), unidadId, request));
    }

    @Operation(summary = "Desactivar unidad de medida", tags = "Unidades de medida")
    @PatchMapping("/unidades-medida/{unidadId}/desactivar")
    public ResponseEntity<UnidadMedidaResponse> desactivarUnidad(
            @PathVariable Long negocioId, @PathVariable Long unidadId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.desactivarUnidad(negocioId, usuario.id(), unidadId));
    }

    @Operation(summary = "Reactivar unidad de medida", tags = "Unidades de medida")
    @PatchMapping("/unidades-medida/{unidadId}/reactivar")
    public ResponseEntity<UnidadMedidaResponse> reactivarUnidad(
            @PathVariable Long negocioId, @PathVariable Long unidadId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.reactivarUnidad(negocioId, usuario.id(), unidadId));
    }

    @Operation(summary = "Listar proveedores", tags = "Proveedores")
    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorResponse>> listarProveedores(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(catalogoService.listarProveedores(negocioId, usuario.id()));
    }

    @Operation(
            summary = "Crear proveedor",
            tags = "Proveedores",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Distribuidora Central",
                                      "telefono": "+56912345678",
                                      "email": "ventas@central.example"
                                    }
                                    """))))
    @PostMapping("/proveedores")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProveedorResponse> crearProveedor(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.crearProveedor(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Actualizar proveedor",
            tags = "Proveedores",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Distribuidora Central",
                                      "telefono": "+56912345678",
                                      "email": "contacto@central.example"
                                    }
                                    """))))
    @PutMapping("/proveedores/{proveedorId}")
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            @PathVariable Long negocioId, @PathVariable Long proveedorId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(catalogoService.actualizarProveedor(
                negocioId, usuario.id(), proveedorId, request));
    }

    @Operation(summary = "Desactivar proveedor", tags = "Proveedores")
    @PatchMapping("/proveedores/{proveedorId}/desactivar")
    public ResponseEntity<ProveedorResponse> desactivarProveedor(
            @PathVariable Long negocioId, @PathVariable Long proveedorId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.desactivarProveedor(negocioId, usuario.id(), proveedorId));
    }

    @Operation(summary = "Reactivar proveedor", tags = "Proveedores")
    @PatchMapping("/proveedores/{proveedorId}/reactivar")
    public ResponseEntity<ProveedorResponse> reactivarProveedor(
            @PathVariable Long negocioId, @PathVariable Long proveedorId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.reactivarProveedor(negocioId, usuario.id(), proveedorId));
    }

    @Operation(summary = "Listar productos", tags = "Productos",
            description = "Filtra opcionalmente los productos por proveedor, categoría y estado.")
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponse>> listarProductos(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Parameter(description = "Filtrar por ID de proveedor", example = "1")
            @RequestParam(required = false) Long proveedorId,
            @Parameter(description = "Filtrar por ID de categoría", example = "1")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Incluir solo productos activos")
            @RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(
                catalogoService.listarProductos(
                        negocioId, usuario.id(), proveedorId, categoriaId, soloActivos));
    }

    @Operation(summary = "Consultar producto", tags = "Productos")
    @GetMapping("/productos/{productoId}")
    public ResponseEntity<ProductoResponse> consultarProducto(
            @PathVariable Long negocioId, @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.consultarProducto(negocioId, usuario.id(), productoId));
    }

    @Operation(
            summary = "Crear producto",
            tags = "Productos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "categoriaId": 1,
                                      "proveedorId": 1,
                                      "unidadMedidaId": 1,
                                      "codigoBarras": "7501234567890",
                                      "nombre": "Agua mineral",
                                      "descripcion": "Botella de 1 litro",
                                      "precioCosto": 10.50,
                                      "precioVenta": 15.00,
                                      "stockMinimo": 5.000
                                    }
                                    """))))
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductoResponse> crearProducto(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CrearProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.crearProducto(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Actualizar producto",
            tags = "Productos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "categoriaId": 1,
                                      "proveedorId": 1,
                                      "codigoBarras": "7501234567890",
                                      "nombre": "Agua mineral",
                                      "descripcion": "Botella de 1 litro",
                                      "precioCosto": 10.50,
                                      "precioVenta": 15.00,
                                      "stockMinimo": 5.000
                                    }
                                    """))))
    @PutMapping("/productos/{productoId}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long negocioId, @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody ActualizarProductoRequest request) {
        return ResponseEntity.ok(catalogoService.actualizarProducto(
                negocioId, usuario.id(), productoId, request));
    }

    @Operation(summary = "Desactivar producto", tags = "Productos")
    @PatchMapping("/productos/{productoId}/desactivar")
    public ResponseEntity<ProductoResponse> desactivarProducto(
            @PathVariable Long negocioId, @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.desactivarProducto(negocioId, usuario.id(), productoId));
    }

    @Operation(summary = "Reactivar producto", tags = "Productos")
    @PatchMapping("/productos/{productoId}/reactivar")
    public ResponseEntity<ProductoResponse> reactivarProducto(
            @PathVariable Long negocioId, @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                catalogoService.reactivarProducto(negocioId, usuario.id(), productoId));
    }
}

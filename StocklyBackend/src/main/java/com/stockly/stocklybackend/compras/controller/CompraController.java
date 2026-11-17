package com.stockly.stocklybackend.compras.controller;

import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.compras.dto.CompraResponse;
import com.stockly.stocklybackend.compras.dto.CompraResumenResponse;
import com.stockly.stocklybackend.compras.dto.CrearCompraRequest;
import com.stockly.stocklybackend.compras.dto.HistorialCostoResponse;
import com.stockly.stocklybackend.compras.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/negocios/{negocioId}/compras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CompraController {
    private final CompraService compraService;

    @Operation(
            summary = "Registrar compra",
            description = "Registra una compra, suma sus cantidades al inventario y actualiza "
                    + "el costo de los productos.",
            tags = "Compras",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "proveedorId": 1,
                                      "numeroFactura": "FAC-2026-001",
                                      "lineas": [
                                        {
                                          "productoId": 1,
                                          "cantidad": 12,
                                          "precioCosto": 10.50
                                        },
                                        {
                                          "productoId": 2,
                                          "cantidad": 2.500,
                                          "precioCosto": 34.90
                                        }
                                      ]
                                    }
                                    """))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompraResponse> registrar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CrearCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.registrar(negocioId, usuario.id(), request));
    }

    @Operation(summary = "Listar compras", tags = "Compras",
            description = "Permite filtrar por proveedor y rango de fechas. "
                    + "Devuelve 20 resultados por página, ordenados por fecha descendente.")
    @GetMapping
    public ResponseEntity<Page<CompraResumenResponse>> listar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Parameter(description = "Filtrar por ID de proveedor", example = "1")
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false)
            @Parameter(description = "Fecha y hora inicial ISO-8601",
                    example = "2026-01-01T00:00:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @Parameter(description = "Fecha y hora final ISO-8601",
                    example = "2026-12-31T23:59:59")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true)
            Pageable pageable) {
        return ResponseEntity.ok(
                compraService.listar(negocioId, usuario.id(), proveedorId, desde, hasta, pageable));
    }

    @Operation(summary = "Consultar compra", tags = "Compras")
    @GetMapping("/{compraId}")
    public ResponseEntity<CompraResponse> consultar(
            @PathVariable Long negocioId, @PathVariable Long compraId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(compraService.consultar(negocioId, usuario.id(), compraId));
    }

    @Operation(summary = "Consultar historial de costos de un producto", tags = "Compras",
            description = "Devuelve las compras del producto ordenadas de la más reciente "
                    + "a la más antigua, con 20 resultados por página.")
    @GetMapping("/productos/{productoId}/historial-costos")
    public ResponseEntity<Page<HistorialCostoResponse>> historialCostos(
            @PathVariable Long negocioId, @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @PageableDefault(size = 20)
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(
                compraService.historialCostos(negocioId, usuario.id(), productoId, pageable));
    }
}

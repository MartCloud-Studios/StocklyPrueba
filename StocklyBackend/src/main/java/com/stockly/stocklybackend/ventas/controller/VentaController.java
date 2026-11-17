package com.stockly.stocklybackend.ventas.controller;

import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.ventas.dto.CrearVentaRequest;
import com.stockly.stocklybackend.ventas.dto.VentaResponse;
import com.stockly.stocklybackend.ventas.dto.VentaResumenResponse;
import com.stockly.stocklybackend.ventas.service.VentaService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/negocios/{negocioId}/ventas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VentaController {
    private final VentaService ventaService;

    @Operation(
            summary = "Registrar venta",
            description = "Registra una venta y descuenta las cantidades del inventario. "
                    + "Los precios se toman del catálogo al momento de registrar.",
            tags = "Ventas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "metodoPago": "EFECTIVO",
                                      "lineas": [
                                        {
                                          "productoId": 1,
                                          "cantidad": 2
                                        },
                                        {
                                          "productoId": 2,
                                          "cantidad": 1.500
                                        }
                                      ]
                                    }
                                    """))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CrearVentaRequest request) {
        return ventaService.registrar(negocioId, usuario.id(), request);
    }

    @Operation(summary = "Listar ventas", tags = "Ventas",
            description = "Permite filtrar por vendedor y rango de fechas. "
                    + "Devuelve 20 resultados por página, ordenados por fecha descendente.")
    @GetMapping
    public Page<VentaResumenResponse> listar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Parameter(description = "Filtrar por ID del vendedor", example = "1")
            @RequestParam(required = false) Long vendedorId,
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
        return ventaService.listar(negocioId, usuario.id(), vendedorId, desde, hasta, pageable);
    }

    @Operation(summary = "Consultar venta", tags = "Ventas",
            description = "Consulta una venta y sus líneas. Un vendedor solo puede consultar "
                    + "sus propias ventas.")
    @GetMapping("/{ventaId}")
    public VentaResponse consultar(
            @PathVariable Long negocioId, @PathVariable Long ventaId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ventaService.consultar(negocioId, usuario.id(), ventaId);
    }
}
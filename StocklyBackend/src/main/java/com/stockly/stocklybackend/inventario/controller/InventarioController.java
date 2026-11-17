package com.stockly.stocklybackend.inventario.controller;

import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.inventario.dto.AjusteRequest;
import com.stockly.stocklybackend.inventario.dto.EntradaRequest;
import com.stockly.stocklybackend.inventario.dto.MovimientoResponse;
import com.stockly.stocklybackend.inventario.dto.StockResponse;
import com.stockly.stocklybackend.inventario.entity.TipoMovimiento;
import com.stockly.stocklybackend.inventario.service.InventarioService;
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
import java.util.List;

@RestController
@RequestMapping("/api/negocios/{negocioId}/inventario")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {
    private final InventarioService inventarioService;

    @Operation(summary = "Listar existencias", tags = "Inventario",
            description = "Devuelve las existencias del negocio. Usa soloBajos para mostrar "
                    + "productos en o por debajo de su stock mínimo.")
    @GetMapping("/stock")
    public ResponseEntity<List<StockResponse>> listarStock(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Parameter(description = "Mostrar únicamente existencias bajas",
                    example = "true")
            @RequestParam(defaultValue = "false") boolean soloBajos) {
        return ResponseEntity.ok(inventarioService.listarStock(negocioId, usuario.id(), soloBajos));
    }

    @Operation(summary = "Consultar existencia de un producto", tags = "Inventario")
    @GetMapping("/stock/{productoId}")
    public ResponseEntity<StockResponse> consultarStock(
            @PathVariable Long negocioId,
            @PathVariable Long productoId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                inventarioService.consultarStock(negocioId, usuario.id(), productoId));
    }

    @Operation(summary = "Listar movimientos de inventario", tags = "Inventario",
            description = "Filtra opcionalmente por producto, tipo y fechas. "
                    + "Devuelve 20 resultados por página, ordenados por fecha descendente.")
    @GetMapping("/movimientos")
    public ResponseEntity<Page<MovimientoResponse>> listarMovimientos(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Parameter(description = "Filtrar por ID de producto", example = "1")
            @RequestParam(required = false) Long productoId,
            @Parameter(description = "Tipo de movimiento", example = "ENTRADA")
            @RequestParam(required = false) TipoMovimiento tipo,
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
        return ResponseEntity.ok(inventarioService.listarMovimientos(
                negocioId, usuario.id(), productoId, tipo, desde, hasta, pageable));
    }

    @Operation(
            summary = "Registrar stock inicial",
            tags = "Inventario",
            description = "Crea el movimiento inicial de inventario para un producto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "productoId": 1,
                                      "cantidad": 25,
                                      "motivo": "Inventario inicial"
                                    }
                                    """))))
    @PostMapping("/stock-inicial")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MovimientoResponse> registrarStockInicial(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody EntradaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarStockInicial(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Registrar entrada manual",
            tags = "Inventario",
            description = "Suma unidades al stock del producto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "productoId": 1,
                                      "cantidad": 10,
                                      "motivo": "Reposición manual"
                                    }
                                    """))))
    @PostMapping("/entradas")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MovimientoResponse> registrarEntrada(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody EntradaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarEntradaManual(negocioId, usuario.id(), request));
    }

    @Operation(
            summary = "Ajustar inventario",
            tags = "Inventario",
            description = "Registra la cantidad real contada y ajusta la existencia.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "productoId": 1,
                                      "cantidadReal": 18,
                                      "motivo": "Conteo físico"
                                    }
                                    """))))
    @PostMapping("/ajustes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MovimientoResponse> registrarAjuste(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody AjusteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarAjuste(negocioId, usuario.id(), request));
    }
}
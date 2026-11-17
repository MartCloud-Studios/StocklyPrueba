package com.stockly.stocklybackend.inventario.dto;

import com.stockly.stocklybackend.inventario.entity.MovimientoInventario;
import com.stockly.stocklybackend.inventario.entity.TipoMovimiento;
import com.stockly.stocklybackend.inventario.entity.TipoReferencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        Long productoId,
        String productoNombre,
        TipoMovimiento tipo,
        BigDecimal cantidad,
        BigDecimal stockAnterior,
        BigDecimal stockResultante,
        String motivo,
        TipoReferencia referenciaTipo,
        Long referenciaId,
        Long usuarioId,
        String usuarioNombre,
        LocalDateTime createdAt
) {
    public static MovimientoResponse from(MovimientoInventario m) {
        return new MovimientoResponse(
                m.getId(),
                m.getProducto().getId(),
                m.getProducto().getNombre(),
                m.getTipo(),
                m.getCantidad(),
                m.getStockAnterior(),
                m.getStockResultante(),
                m.getMotivo(),
                m.getReferenciaTipo(),
                m.getReferenciaId(),
                m.getUsuario().getId(),
                m.getUsuario().getNombre(),
                m.getCreatedAt());
    }
}
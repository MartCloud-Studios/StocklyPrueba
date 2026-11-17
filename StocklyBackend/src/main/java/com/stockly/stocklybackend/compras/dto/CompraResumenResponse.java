package com.stockly.stocklybackend.compras.dto;

import com.stockly.stocklybackend.compras.entity.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Fila del listado: la compra sin sus líneas. */
public record CompraResumenResponse(
        Long id,
        Long proveedorId,
        String proveedorNombre,
        Long usuarioId,
        String usuarioNombre,
        String numeroFactura,
        BigDecimal total,
        EstadoCompra estado,
        LocalDateTime createdAt
) {}
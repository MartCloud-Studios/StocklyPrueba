package com.stockly.stocklybackend.compras.dto;

import com.stockly.stocklybackend.compras.entity.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Long id,
        Long negocioId,
        Long proveedorId,
        String proveedorNombre,
        Long usuarioId,
        String usuarioNombre,
        String numeroFactura,
        BigDecimal total,
        EstadoCompra estado,
        LocalDateTime createdAt,
        List<DetalleCompraResponse> lineas
) {}
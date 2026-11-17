package com.stockly.stocklybackend.ventas.dto;

import com.stockly.stocklybackend.ventas.entity.EstadoVenta;
import com.stockly.stocklybackend.ventas.entity.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        Long negocioId,
        Long usuarioId,
        String usuarioNombre,
        MetodoPago metodoPago,
        BigDecimal total,
        EstadoVenta estado,
        LocalDateTime createdAt,
        List<DetalleVentaResponse> lineas
) {}
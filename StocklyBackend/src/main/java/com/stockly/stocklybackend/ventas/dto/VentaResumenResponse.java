package com.stockly.stocklybackend.ventas.dto;

import com.stockly.stocklybackend.ventas.entity.EstadoVenta;
import com.stockly.stocklybackend.ventas.entity.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Fila del listado
public record VentaResumenResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        MetodoPago metodoPago,
        BigDecimal total,
        EstadoVenta estado,
        LocalDateTime createdAt
) {}
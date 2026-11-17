package com.stockly.stocklybackend.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistorialCostoResponse(
        Long compraId,
        LocalDateTime fecha,
        Long proveedorId,
        String proveedorNombre,
        BigDecimal cantidad,
        BigDecimal precioCosto
) {}
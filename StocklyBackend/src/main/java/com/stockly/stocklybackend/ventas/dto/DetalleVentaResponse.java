package com.stockly.stocklybackend.ventas.dto;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Long productoId,
        String productoNombre,
        BigDecimal cantidad,
        BigDecimal precioVenta,
        BigDecimal precioCosto,
        BigDecimal subtotal
) {}
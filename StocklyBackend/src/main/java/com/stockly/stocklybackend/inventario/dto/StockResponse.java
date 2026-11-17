package com.stockly.stocklybackend.inventario.dto;

import java.math.BigDecimal;

public record StockResponse(
        Long productoId,
        String nombre,
        String unidadMedida,
        BigDecimal cantidad,
        BigDecimal stockMinimo,
        boolean bajoMinimo
) {}
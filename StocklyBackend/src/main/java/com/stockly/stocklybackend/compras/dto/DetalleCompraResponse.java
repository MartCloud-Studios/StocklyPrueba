package com.stockly.stocklybackend.compras.dto;

import java.math.BigDecimal;

public record DetalleCompraResponse(
        Long productoId,
        String productoNombre,
        BigDecimal cantidad,
        BigDecimal precioCosto,
        BigDecimal subtotal
) {}
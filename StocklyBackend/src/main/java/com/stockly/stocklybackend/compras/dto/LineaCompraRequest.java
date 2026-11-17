package com.stockly.stocklybackend.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LineaCompraRequest(
        @NotNull Long productoId,
        @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
        BigDecimal cantidad,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
        BigDecimal precioCosto
) {}

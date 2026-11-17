package com.stockly.stocklybackend.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** El precio no se envía: lo pone el servidor. */
public record LineaVentaRequest(
        @NotNull Long productoId,
        @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
        BigDecimal cantidad
) {}
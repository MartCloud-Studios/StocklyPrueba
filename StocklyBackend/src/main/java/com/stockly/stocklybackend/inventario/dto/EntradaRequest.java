package com.stockly.stocklybackend.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Stock inicial o entrada manual. El motivo es opcional. */
public record EntradaRequest(
        @NotNull Long productoId,
        @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
        BigDecimal cantidad,
        @Size(max = 255) String motivo
) {}
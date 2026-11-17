package com.stockly.stocklybackend.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AjusteRequest(
        @NotNull Long productoId,
        @NotNull @DecimalMin("0.000") @Digits(integer = 9, fraction = 3)
        BigDecimal cantidadReal,
        @Size(max = 255) String motivo
) {}
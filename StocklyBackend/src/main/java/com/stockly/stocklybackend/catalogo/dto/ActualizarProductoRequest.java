package com.stockly.stocklybackend.catalogo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActualizarProductoRequest(
        Long categoriaId,
        Long proveedorId,
        @Size(max = 50) String codigoBarras,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 500) String descripcion,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
        BigDecimal precioCosto,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
        BigDecimal precioVenta,
        @NotNull @DecimalMin("0.000") @Digits(integer = 9, fraction = 3)
        BigDecimal stockMinimo
) {}

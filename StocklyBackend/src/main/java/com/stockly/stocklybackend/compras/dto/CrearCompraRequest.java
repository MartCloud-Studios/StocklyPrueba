package com.stockly.stocklybackend.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearCompraRequest(
        Long proveedorId,
        @Size(max = 50) String numeroFactura,
        @NotEmpty @Valid List<LineaCompraRequest> lineas
) {}
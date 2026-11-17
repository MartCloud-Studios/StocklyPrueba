package com.stockly.stocklybackend.ventas.dto;

import com.stockly.stocklybackend.ventas.entity.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearVentaRequest(
        @NotNull MetodoPago metodoPago,
        @NotEmpty @Valid List<LineaVentaRequest> lineas
) {}
package com.stockly.stocklybackend.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadMedidaRequest(
        @NotBlank @Size(max = 50) String nombre
) {}

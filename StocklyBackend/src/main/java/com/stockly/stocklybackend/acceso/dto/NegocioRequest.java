package com.stockly.stocklybackend.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NegocioRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 30) String telefono,
        @Size(max = 255) String direccion
){}

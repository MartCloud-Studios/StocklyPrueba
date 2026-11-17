package com.stockly.stocklybackend.catalogo.dto;

import java.time.LocalDateTime;

public record ProveedorResponse(
        Long id,
        Long negocioId,
        String nombre,
        String telefono,
        String email,
        boolean activo,
        LocalDateTime createdAt
) {}

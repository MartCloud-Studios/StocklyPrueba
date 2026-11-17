package com.stockly.stocklybackend.catalogo.dto;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        Long negocioId,
        String nombre,
        boolean activo,
        LocalDateTime createdAt
) {}

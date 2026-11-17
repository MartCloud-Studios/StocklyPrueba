package com.stockly.stocklybackend.catalogo.dto;

import java.time.LocalDateTime;

public record UnidadMedidaResponse(
        Long id,
        Long negocioId,
        String nombre,
        boolean base,
        boolean admiteDecimales,
        boolean activo,
        LocalDateTime createdAt
) {}

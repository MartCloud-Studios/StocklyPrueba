package com.stockly.stocklybackend.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        Long negocioId,
        Long categoriaId,
        String categoriaNombre,
        Long proveedorId,
        String proveedorNombre,
        Long unidadMedidaId,
        String unidadMedidaNombre,
        boolean unidadBase,
        String codigoBarras,
        String nombre,
        String descripcion,
        BigDecimal precioCosto,
        BigDecimal precioVenta,
        BigDecimal stockMinimo,
        boolean activo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

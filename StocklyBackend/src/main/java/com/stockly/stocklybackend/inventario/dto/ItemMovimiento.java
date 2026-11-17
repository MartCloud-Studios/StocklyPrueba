package com.stockly.stocklybackend.inventario.dto;

import java.math.BigDecimal;

/** Producto y cantidad, para operaciones con varios ítems (por ejemplo, una venta). */
public record ItemMovimiento(Long productoId, BigDecimal cantidad) {}
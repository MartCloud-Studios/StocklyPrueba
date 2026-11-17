package com.stockly.stocklybackend.ventas.entity;

import com.stockly.stocklybackend.catalogo.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_detalle_venta_producto",
                columnNames = {"venta_id", "producto_id"}))
@Getter
@Setter
@NoArgsConstructor
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    /** Precio unitario de venta al momento de la venta. */
    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    /** Costo unitario del producto al momento de la venta. */
    @Column(name = "precio_costo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCosto;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
}
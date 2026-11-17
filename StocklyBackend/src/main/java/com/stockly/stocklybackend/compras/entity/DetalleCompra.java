package com.stockly.stocklybackend.compras.entity;

import com.stockly.stocklybackend.catalogo.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compras",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_detalle_compra_producto",
                columnNames = {"compra_id", "producto_id"}))
@Getter
@Setter
@NoArgsConstructor
public class DetalleCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    /** Costo unitario pagado en esta compra. */
    @Column(name = "precio_costo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCosto;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
}
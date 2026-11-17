package com.stockly.stocklybackend.inventario.entity;

import com.stockly.stocklybackend.acceso.entity.Negocio;
import com.stockly.stocklybackend.acceso.entity.Usuario;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario",
        indexes = {
                @Index(name = "idx_movimiento_negocio_producto_fecha",
                        columnList = "negocio_id,producto_id,created_at"),
                @Index(name = "idx_movimiento_referencia",
                        columnList = "referencia_tipo,referencia_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "negocio_id", nullable = false, updatable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false, updatable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private TipoMovimiento tipo;

    /** Positiva en ENTRADA y SALIDA; con signo en AJUSTE. */
    @Column(nullable = false, updatable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", nullable = false, updatable = false, precision = 12, scale = 3)
    private BigDecimal stockAnterior;

    @Column(name = "stock_resultante", nullable = false, updatable = false, precision = 12, scale = 3)
    private BigDecimal stockResultante;

    @Column(updatable = false, length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "referencia_tipo", updatable = false, length = 20)
    private TipoReferencia referenciaTipo;

    @Column(name = "referencia_id", updatable = false)
    private Long referenciaId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MovimientoInventario(Negocio negocio, Producto producto, Usuario usuario,
                                TipoMovimiento tipo, BigDecimal cantidad,
                                BigDecimal stockAnterior, BigDecimal stockResultante,
                                String motivo, TipoReferencia referenciaTipo,
                                Long referenciaId) {
        this.negocio = negocio;
        this.producto = producto;
        this.usuario = usuario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockResultante = stockResultante;
        this.motivo = motivo;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
    }
}
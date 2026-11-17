package com.stockly.stocklybackend.catalogo.entity;

import com.stockly.stocklybackend.acceso.entity.Negocio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "unidades_medida",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_unidad_negocio_nombre",
                columnNames = {"negocio_id", "nombre"}))
@Getter
@Setter
@NoArgsConstructor
public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public boolean isBase() {
        return negocio == null;
    }

    @Transient
    public boolean admiteDecimales() {
        return isBase() ? false : true;
    }
}

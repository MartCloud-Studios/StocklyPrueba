package com.stockly.stocklybackend.ventas.repository;

import com.stockly.stocklybackend.ventas.entity.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VentaRepository
        extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    /** Trae el usuario en la misma consulta para no hacer una consulta por fila. */
    @Override
    @EntityGraph(attributePaths = {"usuario"})
    Page<Venta> findAll(Specification<Venta> spec, Pageable pageable);

    /** Venta completa: usuario, líneas y sus productos. */
    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    Optional<Venta> findWithDetallesByIdAndNegocioId(Long id, Long negocioId);
}
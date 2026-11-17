package com.stockly.stocklybackend.compras.repository;

import com.stockly.stocklybackend.compras.entity.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CompraRepository
        extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {

    /** Trae proveedor y usuario en la misma consulta para no hacer una consulta por fila. */
    @Override
    @EntityGraph(attributePaths = {"proveedor", "usuario"})
    Page<Compra> findAll(Specification<Compra> spec, Pageable pageable);

    /** Compra completa: proveedor, usuario, líneas y sus productos. */
    @EntityGraph(attributePaths = {"proveedor", "usuario", "detalles", "detalles.producto"})
    Optional<Compra> findWithDetallesByIdAndNegocioId(Long id, Long negocioId);
}
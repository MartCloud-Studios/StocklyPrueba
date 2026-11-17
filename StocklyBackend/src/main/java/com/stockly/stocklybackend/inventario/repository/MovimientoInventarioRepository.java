package com.stockly.stocklybackend.inventario.repository;

import com.stockly.stocklybackend.inventario.entity.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Long>,
        JpaSpecificationExecutor<MovimientoInventario> {

    boolean existsByNegocioIdAndProductoId(Long negocioId, Long productoId);

    /** Trae producto y usuario en la misma consulta para no hacer una consulta por fila. */
    @Override
    @EntityGraph(attributePaths = {"producto", "usuario"})
    Page<MovimientoInventario> findAll(Specification<MovimientoInventario> spec, Pageable pageable);
}
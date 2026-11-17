package com.stockly.stocklybackend.catalogo.repository;

import com.stockly.stocklybackend.catalogo.entity.Producto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository
        extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    List<Producto> findByNegocioIdOrderByNombreAsc(Long negocioId);
    Optional<Producto> findByIdAndNegocioId(Long id, Long negocioId);
    Optional<Producto> findByIdAndNegocioIdAndActivoTrue(Long id, Long negocioId);
    boolean existsByNegocioIdAndCodigoBarras(Long negocioId, String codigoBarras);
    boolean existsByNegocioIdAndCodigoBarrasAndIdNot(
            Long negocioId, String codigoBarras, Long id);

    /** Trae categoría, proveedor y medida en la misma consulta para no hacer una por producto. */
    @Override
    @EntityGraph(attributePaths = {"categoria", "proveedor", "unidadMedida"})
    List<Producto> findAll(Specification<Producto> spec, Sort sort);
}

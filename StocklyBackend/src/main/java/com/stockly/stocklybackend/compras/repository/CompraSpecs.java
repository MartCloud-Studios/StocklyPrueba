package com.stockly.stocklybackend.compras.repository;

import com.stockly.stocklybackend.compras.entity.Compra;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Filtros opcionales del listado de compras. */
public final class CompraSpecs {

    private CompraSpecs(){}
    public static Specification<Compra> filtrar(Long negocioId, Long proveedorId,
                                                LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("negocio").get("id"), negocioId));
            if (proveedorId != null) {
                filtros.add(cb.equal(root.get("proveedor").get("id"), proveedorId));
            }
            if (desde != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.get("createdAt"), desde));
            }
            if (hasta != null) {
                filtros.add(cb.lessThan(root.get("createdAt"), hasta));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }
}

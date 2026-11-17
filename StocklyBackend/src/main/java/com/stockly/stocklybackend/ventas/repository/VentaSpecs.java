package com.stockly.stocklybackend.ventas.repository;

import com.stockly.stocklybackend.ventas.entity.Venta;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Filtros opcionales del listado de ventas. */
public final class VentaSpecs {

    private VentaSpecs() {
    }

    public static Specification<Venta> filtrar(Long negocioId, Long usuarioId,
                                               LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("negocio").get("id"), negocioId));
            if (usuarioId != null) {
                filtros.add(cb.equal(root.get("usuario").get("id"), usuarioId));
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
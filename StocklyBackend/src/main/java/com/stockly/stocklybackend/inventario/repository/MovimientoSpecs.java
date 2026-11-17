package com.stockly.stocklybackend.inventario.repository;

import com.stockly.stocklybackend.inventario.entity.MovimientoInventario;
import com.stockly.stocklybackend.inventario.entity.TipoMovimiento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Filtros opcionales del historial de movimientos. */
public final class MovimientoSpecs {

    private MovimientoSpecs() {}
    public static Specification<MovimientoInventario> filtrar(Long negocioId, Long productoId,
                                                              TipoMovimiento tipo,
                                                              LocalDateTime desde,
                                                              LocalDateTime hasta) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("negocio").get("id"), negocioId));
            if (productoId != null) {
                filtros.add(cb.equal(root.get("producto").get("id"), productoId));
            }
            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipo"), tipo));
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
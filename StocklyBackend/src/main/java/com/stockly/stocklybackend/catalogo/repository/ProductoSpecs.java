package com.stockly.stocklybackend.catalogo.repository;

import com.stockly.stocklybackend.catalogo.entity.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Filtros opcionales del listado de productos. */
public final class ProductoSpecs {

    private ProductoSpecs() {
    }

    public static Specification<Producto> filtrar(Long negocioId, Long proveedorId,
                                                  Long categoriaId, boolean soloActivos) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("negocio").get("id"), negocioId));
            if (proveedorId != null) {
                filtros.add(cb.equal(root.get("proveedor").get("id"), proveedorId));
            }
            if (categoriaId != null) {
                filtros.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (soloActivos) {
                filtros.add(cb.isTrue(root.get("activo")));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }
}

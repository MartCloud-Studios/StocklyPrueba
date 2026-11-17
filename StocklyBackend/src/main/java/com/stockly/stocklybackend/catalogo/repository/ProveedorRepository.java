package com.stockly.stocklybackend.catalogo.repository;

import com.stockly.stocklybackend.catalogo.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByNegocioIdOrderByNombreAsc(Long negocioId);
    Optional<Proveedor> findByIdAndNegocioId(Long id, Long negocioId);
    boolean existsByNegocioIdAndNombreIgnoreCase(Long negocioId, String nombre);
    boolean existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
            Long negocioId, String nombre, Long id);
}

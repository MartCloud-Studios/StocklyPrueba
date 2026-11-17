package com.stockly.stocklybackend.catalogo.repository;

import com.stockly.stocklybackend.catalogo.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    List<UnidadMedida> findByNegocioIdOrNegocioIsNullOrderByNombreAsc(Long negocioId);
    Optional<UnidadMedida> findByIdAndNegocioId(Long id, Long negocioId);
    Optional<UnidadMedida> findByNegocioIsNullAndNombreIgnoreCase(String nombre);
    boolean existsByNegocioIdAndNombreIgnoreCase(Long negocioId, String nombre);
    boolean existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
            Long negocioId, String nombre, Long id);
}

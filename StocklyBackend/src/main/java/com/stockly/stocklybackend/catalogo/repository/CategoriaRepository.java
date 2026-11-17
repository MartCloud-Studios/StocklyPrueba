package com.stockly.stocklybackend.catalogo.repository;

import com.stockly.stocklybackend.catalogo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByNegocioIdOrderByNombreAsc(Long negocioId);
    Optional<Categoria> findByIdAndNegocioId(Long id, Long negocioId);
    boolean existsByNegocioIdAndNombreIgnoreCase(Long negocioId, String nombre);
    boolean existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
            Long negocioId, String nombre, Long id);
}

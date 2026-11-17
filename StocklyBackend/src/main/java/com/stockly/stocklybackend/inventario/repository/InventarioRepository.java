package com.stockly.stocklybackend.inventario.repository;

import com.stockly.stocklybackend.inventario.entity.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i "
            + "where i.negocio.id = :negocioId and i.producto.id = :productoId")
    Optional<Inventario> buscarBloqueando(@Param("negocioId") Long negocioId,
                                          @Param("productoId") Long productoId);

    Optional<Inventario> findByNegocioIdAndProductoId(Long negocioId, Long productoId);

    List<Inventario> findByNegocioId(Long negocioId);
}
package com.stockly.stocklybackend.compras.repository;

import com.stockly.stocklybackend.compras.entity.DetalleCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    /** Historial de costos de un producto: sus líneas de compra, de la más reciente a la más antigua. */
    @EntityGraph(attributePaths = {"compra", "compra.proveedor"})
    Page<DetalleCompra> findByCompraNegocioIdAndProductoIdOrderByCompraCreatedAtDesc(
            Long negocioId, Long productoId, Pageable pageable);
}
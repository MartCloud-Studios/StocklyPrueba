package com.stockly.stocklybackend.acceso.repository;

import com.stockly.stocklybackend.acceso.entity.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {
    Negocio findByNombre(String nombre);
}

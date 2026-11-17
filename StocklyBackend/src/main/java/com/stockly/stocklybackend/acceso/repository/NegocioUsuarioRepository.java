package com.stockly.stocklybackend.acceso.repository;

import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NegocioUsuarioRepository extends JpaRepository<NegocioUsuario, Long> {

    List<NegocioUsuario> findByUsuarioIdAndActivoTrue(Long usuarioId);
    Optional<NegocioUsuario> findByUsuarioIdAndNegocioId(Long usuarioId, Long negocioId);
    List<NegocioUsuario> findByNegocioId(Long negocioId);
    long countByNegocioIdAndRolNombreAndActivoTrue(Long negocioId, NombreRol rolNombre);
}

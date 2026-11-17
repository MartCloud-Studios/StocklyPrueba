package com.stockly.stocklybackend.acceso.service;

import com.stockly.stocklybackend.acceso.dto.AgregarUsuarioRequest;
import com.stockly.stocklybackend.acceso.dto.UsuarioNegocioResponse;
import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.entity.Rol;
import com.stockly.stocklybackend.acceso.entity.Usuario;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.acceso.repository.RolRepository;
import com.stockly.stocklybackend.acceso.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UsuarioNegocioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;

    //Lista los usuarios del negocio. Solo el propietario
    @Transactional(readOnly = true)
    public List<UsuarioNegocioResponse> listar(Long negocioId, Long solicitanteId) {
        exigirPropietario(negocioId, solicitanteId);
        return negocioUsuarioRepository.findByNegocioId(negocioId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    //Agrega al negocio a un usuario que YA tiene cuenta, buscándolo por correo
    @Transactional
    public UsuarioNegocioResponse agregar(Long negocioId, Long solicitanteId,
                                          AgregarUsuarioRequest req) {
        NegocioUsuario solicitante = exigirPropietario(negocioId, solicitanteId);
        Rol rol = buscarRol(req.rol());

        String email = req.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "No existe un usuario con ese correo"));

        NegocioUsuario relacion = negocioUsuarioRepository
                .findByUsuarioIdAndNegocioId(usuario.getId(), negocioId)
                .orElse(null);

        if (relacion != null) {
            if (relacion.isActivo()) {
                throw new ResponseStatusException(CONFLICT,
                        "El usuario ya pertenece a este negocio");
            }
            // Estaba desactivado: se reactiva con el nuevo rol (la pareja es única)
            relacion.setActivo(true);
            relacion.setRol(rol);
            return aRespuesta(relacion);
        }

        relacion = new NegocioUsuario();
        relacion.setUsuario(usuario);
        relacion.setNegocio(solicitante.getNegocio());
        relacion.setRol(rol);
        negocioUsuarioRepository.save(relacion);
        return aRespuesta(relacion);
    }

    @Transactional
    public UsuarioNegocioResponse cambiarRol(Long negocioId, Long solicitanteId,
                                             Long usuarioId, NombreRol nuevoRol) {
        exigirPropietario(negocioId, solicitanteId);
        NegocioUsuario destino = buscarRelacion(usuarioId, negocioId);
        Rol rol = buscarRol(nuevoRol);

        // Si deja de ser propietario, debe quedar otro
        if (esPropietario(destino) && nuevoRol != NombreRol.PROPIETARIO) {
            exigirOtroPropietario(negocioId);
        }

        destino.setRol(rol);
        return aRespuesta(destino);
    }

    @Transactional
    public UsuarioNegocioResponse desactivar(Long negocioId, Long solicitanteId, Long usuarioId) {
        exigirPropietario(negocioId, solicitanteId);
        NegocioUsuario destino = buscarRelacion(usuarioId, negocioId);

        if (esPropietario(destino) && destino.isActivo()) {
            exigirOtroPropietario(negocioId);
        }

        destino.setActivo(false);
        return aRespuesta(destino);
    }

    // ---------- Reglas auxiliares ----------

    /** El solicitante debe ser PROPIETARIO activo de un negocio activo. */
    private NegocioUsuario exigirPropietario(Long negocioId, Long usuarioId) {
        NegocioUsuario nu = negocioUsuarioRepository
                .findByUsuarioIdAndNegocioId(usuarioId, negocioId)
                .filter(NegocioUsuario::isActivo)
                .filter(x -> x.getNegocio().isActivo())
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN,
                        "No perteneces a este negocio"));
        if (!esPropietario(nu)) {
            throw new ResponseStatusException(FORBIDDEN,
                    "Solo el propietario puede gestionar usuarios");
        }
        return nu;
    }

    /** Antes de quitar o degradar a un propietario, debe quedar al menos otro activo. */
    private void exigirOtroPropietario(Long negocioId) {
        long activos = negocioUsuarioRepository
                .countByNegocioIdAndRolNombreAndActivoTrue(negocioId, NombreRol.PROPIETARIO);
        if (activos <= 1) {
            throw new ResponseStatusException(CONFLICT,
                    "El negocio debe conservar al menos un propietario activo");
        }
    }

    private boolean esPropietario(NegocioUsuario nu) {
        return nu.getRol().getNombre() == NombreRol.PROPIETARIO;
    }

    private Rol buscarRol(NombreRol nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el rol " + nombre + ": revisa la migración de roles"));
    }

    private NegocioUsuario buscarRelacion(Long usuarioId, Long negocioId) {
        return negocioUsuarioRepository.findByUsuarioIdAndNegocioId(usuarioId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "El usuario no pertenece a este negocio"));
    }

    private UsuarioNegocioResponse aRespuesta(NegocioUsuario nu) {
        return new UsuarioNegocioResponse(
                nu.getUsuario().getId(),
                nu.getUsuario().getNombre(),
                nu.getUsuario().getEmail(),
                nu.getRol().getNombre(),
                nu.isActivo());
    }
}
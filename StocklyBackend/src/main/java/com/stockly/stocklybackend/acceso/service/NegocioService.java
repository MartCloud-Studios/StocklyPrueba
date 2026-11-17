package com.stockly.stocklybackend.acceso.service;

import com.stockly.stocklybackend.acceso.dto.NegocioRequest;
import com.stockly.stocklybackend.acceso.dto.NegocioResumen;
import com.stockly.stocklybackend.acceso.entity.Negocio;
import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.entity.Rol;
import com.stockly.stocklybackend.acceso.entity.Usuario;
import com.stockly.stocklybackend.acceso.repository.NegocioRepository;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.acceso.repository.RolRepository;
import com.stockly.stocklybackend.acceso.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NegocioService {
    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;

    //esto crea el negocio y deja al usuario como PROPIETARIO, en una sola transacción
    @Transactional
    public NegocioResumen crear(Long usuarioId, NegocioRequest req) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        Rol propietario = rolRepository.findByNombre(NombreRol.PROPIETARIO)
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el rol PROPIETARIO"));

        Negocio negocio = new Negocio();
        negocio.setNombre(req.nombre());
        negocio.setTelefono(req.telefono());
        negocio.setDireccion(req.direccion());
        negocioRepository.save(negocio);

        NegocioUsuario relacion = new NegocioUsuario();
        relacion.setUsuario(usuario);
        relacion.setNegocio(negocio);
        relacion.setRol(propietario);
        negocioUsuarioRepository.save(relacion);

        return new NegocioResumen(negocio.getId(), negocio.getNombre(), propietario.getNombre());
    }
}

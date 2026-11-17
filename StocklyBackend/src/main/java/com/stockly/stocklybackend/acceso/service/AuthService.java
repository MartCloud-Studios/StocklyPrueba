package com.stockly.stocklybackend.acceso.service;


import com.stockly.stocklybackend.acceso.dto.*;
import com.stockly.stocklybackend.acceso.entity.Usuario;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.acceso.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;
    private final NegocioService negocioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Registra al usuario y crea su primer negocio, todo en una transacción
    @Transactional
    public LoginResponse registrar(RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(req.nombre());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(req.password()));
        usuarioRepository.save(usuario);

        negocioService.crear(usuario.getId(), new NegocioRequest(req.nombreNegocio(), null, null));
        return respuesta(usuario);
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciales incorrectas"));

        String passwordGuardada = usuario.getPassword();
        boolean credencialesValidas = esHashBcrypt(passwordGuardada)
                ? passwordEncoder.matches(req.password(), passwordGuardada)
                : passwordGuardada.equals(req.password());
        if (!credencialesValidas) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciales incorrectas");
        }
        if (!esHashBcrypt(passwordGuardada)) {
            usuario.setPassword(passwordEncoder.encode(req.password()));
            usuarioRepository.save(usuario);
        }
        return respuesta(usuario);
    }

    private boolean esHashBcrypt(String password) {
        return password.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    }

    private LoginResponse respuesta(Usuario usuario) {
        List<NegocioResumen> negocios = negocioUsuarioRepository
                .findByUsuarioIdAndActivoTrue(usuario.getId()).stream()
                .filter(nu -> nu.getNegocio().isActivo())
                .map(nu -> new NegocioResumen(
                        nu.getNegocio().getId(),
                        nu.getNegocio().getNombre(),
                        nu.getRol().getNombre()))
                .toList();
        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                jwtService.generarToken(usuario.getId(), usuario.getEmail()),
                "Bearer",
                jwtService.getExpirationSeconds(),
                negocios);
    }
}

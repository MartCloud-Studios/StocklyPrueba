package com.stockly.stocklybackend.acceso.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            Base64.getEncoder().encodeToString(
                    "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8)),
            "stockly-api",
            900_000);

    @Test
    void generaYValidaTokenConIdentidadDeUsuario() {
        String token = jwtService.generarToken(42L, "usuario@stockly.com");

        assertEquals(
                new AuthenticatedUser(42L, "usuario@stockly.com"),
                jwtService.validarToken(token));
    }

    @Test
    void rechazaTokenAlterado() {
        String token = jwtService.generarToken(42L, "usuario@stockly.com");

        assertThrows(JWTVerificationException.class,
                () -> jwtService.validarToken(token + "alterado"));
    }
}

package com.stockly.stocklybackend.acceso.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String encodedSecret,
            @Value("${app.jwt.issuer:stockly-api}") String issuer,
            @Value("${app.jwt.expiration-ms:900000}") long expirationMillis) {
        byte[] secret;
        try {
            secret = Base64.getDecoder().decode(encodedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("JWT_SECRET debe ser una clave Base64 válida", exception);
        }
        if (secret.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes");
        }
        if (expirationMillis <= 0) {
            throw new IllegalArgumentException("app.jwt.expiration-ms debe ser positivo");
        }

        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expirationMillis = expirationMillis;
    }

    public String generarToken(Long usuarioId, String email) {
        Instant ahora = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(email)
                .withClaim("userId", usuarioId)
                .withIssuedAt(Date.from(ahora))
                .withExpiresAt(Date.from(ahora.plusMillis(expirationMillis)))
                .sign(algorithm);
    }

    public AuthenticatedUser validarToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token);
        Long usuarioId = jwt.getClaim("userId").asLong();
        String email = jwt.getSubject();
        if (usuarioId == null || email == null || email.isBlank()) {
            throw new IllegalArgumentException("El token no contiene la identidad requerida");
        }
        return new AuthenticatedUser(usuarioId, email);
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }
}

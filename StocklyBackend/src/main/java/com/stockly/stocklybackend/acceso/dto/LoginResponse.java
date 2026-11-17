package com.stockly.stocklybackend.acceso.dto;

import java.util.List;

public record LoginResponse(
        Long usuarioId,
        String nombre,
        String accessToken,
        String tokenType,
        long expiresIn,
        List<NegocioResumen> negocios) {
}

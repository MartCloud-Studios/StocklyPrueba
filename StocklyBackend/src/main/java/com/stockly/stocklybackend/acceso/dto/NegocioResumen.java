package com.stockly.stocklybackend.acceso.dto;

import com.stockly.stocklybackend.acceso.entity.NombreRol;

public record NegocioResumen(Long negocioId, String nombre, NombreRol rol) {
}

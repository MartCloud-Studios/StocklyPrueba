package com.stockly.stocklybackend.acceso.dto;

import com.stockly.stocklybackend.acceso.entity.NombreRol;

public record UsuarioNegocioResponse(Long usuarioId, String nombre, String email, NombreRol rol, boolean activo){}
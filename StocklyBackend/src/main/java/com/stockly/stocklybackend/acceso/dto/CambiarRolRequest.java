package com.stockly.stocklybackend.acceso.dto;

import com.stockly.stocklybackend.acceso.entity.NombreRol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(@NotNull NombreRol rol){}
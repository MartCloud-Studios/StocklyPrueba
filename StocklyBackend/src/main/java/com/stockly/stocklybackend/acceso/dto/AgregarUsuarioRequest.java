package com.stockly.stocklybackend.acceso.dto;

import com.stockly.stocklybackend.acceso.entity.NombreRol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgregarUsuarioRequest(@NotBlank @Email String email, @NotNull NombreRol rol){}

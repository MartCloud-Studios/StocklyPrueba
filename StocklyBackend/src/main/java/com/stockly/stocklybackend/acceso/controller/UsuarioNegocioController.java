package com.stockly.stocklybackend.acceso.controller;

import com.stockly.stocklybackend.acceso.dto.AgregarUsuarioRequest;
import com.stockly.stocklybackend.acceso.dto.CambiarRolRequest;
import com.stockly.stocklybackend.acceso.dto.UsuarioNegocioResponse;
import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.acceso.service.UsuarioNegocioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios/{negocioId}/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UsuarioNegocioController {

    private final UsuarioNegocioService service;

    @Operation(summary = "Listar usuarios del negocio", tags = "Usuarios del negocio")
    @GetMapping
    public ResponseEntity<List<UsuarioNegocioResponse>> listar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(service.listar(negocioId, usuario.id()));
    }

    @Operation(
            summary = "Agregar usuario al negocio",
            tags = "Usuarios del negocio",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "vendedor@example.com",
                                      "rol": "VENDEDOR"
                                    }
                                    """))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioNegocioResponse> agregar(
            @PathVariable Long negocioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody AgregarUsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.agregar(negocioId, usuario.id(), req));
    }

    @Operation(
            summary = "Cambiar rol de usuario",
            tags = "Usuarios del negocio",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "rol": "ALMACENISTA"
                                    }
                                    """))))
    @PutMapping("/{usuarioId}/rol")
    public ResponseEntity<UsuarioNegocioResponse> cambiarRol(
            @PathVariable Long negocioId,
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody CambiarRolRequest req) {
        return ResponseEntity.ok(
                service.cambiarRol(negocioId, usuario.id(), usuarioId, req.rol()));
    }

    @Operation(summary = "Desactivar usuario del negocio", tags = "Usuarios del negocio")
    @PatchMapping("/{usuarioId}/desactivar")
    public ResponseEntity<UsuarioNegocioResponse> desactivar(
            @PathVariable Long negocioId,
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(service.desactivar(negocioId, usuario.id(), usuarioId));
    }
}
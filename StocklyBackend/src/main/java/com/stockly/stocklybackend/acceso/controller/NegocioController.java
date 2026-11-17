package com.stockly.stocklybackend.acceso.controller;

import com.stockly.stocklybackend.acceso.dto.NegocioRequest;
import com.stockly.stocklybackend.acceso.dto.NegocioResumen;
import com.stockly.stocklybackend.acceso.security.AuthenticatedUser;
import com.stockly.stocklybackend.acceso.service.NegocioService;
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

@RestController
@RequestMapping("/api/negocios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NegocioController {
    private final NegocioService negocioService;

    @Operation(
            summary = "Crear negocio",
            tags = "Negocios",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Tienda Ana",
                                      "telefono": "+56912345678",
                                      "direccion": "Av. Principal 123"
                                    }
                                    """))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NegocioResumen> crear(
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @Valid @RequestBody NegocioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(negocioService.crear(usuario.id(), req));
    }
}

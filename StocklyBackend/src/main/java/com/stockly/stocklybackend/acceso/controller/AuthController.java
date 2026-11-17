package com.stockly.stocklybackend.acceso.controller;

import com.stockly.stocklybackend.acceso.dto.LoginRequest;
import com.stockly.stocklybackend.acceso.dto.LoginResponse;
import com.stockly.stocklybackend.acceso.dto.RegistroRequest;
import com.stockly.stocklybackend.acceso.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Registrar usuario",
            tags = "Autenticación",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Ana Pérez",
                                      "email": "ana@example.com",
                                      "password": "Password123",
                                      "nombreNegocio": "Tienda Ana"
                                    }
                                    """))))
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginResponse> registro(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @Operation(
            summary = "Iniciar sesión",
            tags = "Autenticación",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "ana@example.com",
                                      "password": "Password123"
                                    }
                                    """))))
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}

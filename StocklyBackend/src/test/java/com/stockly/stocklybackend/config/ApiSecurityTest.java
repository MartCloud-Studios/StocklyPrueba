package com.stockly.stocklybackend.config;

import com.stockly.stocklybackend.acceso.dto.LoginResponse;
import com.stockly.stocklybackend.acceso.dto.NegocioResumen;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.repository.RolRepository;
import com.stockly.stocklybackend.acceso.service.AuthService;
import com.stockly.stocklybackend.acceso.service.JwtService;
import com.stockly.stocklybackend.acceso.service.NegocioService;
import com.stockly.stocklybackend.acceso.service.UsuarioNegocioService;
import com.stockly.stocklybackend.catalogo.repository.UnidadMedidaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {
                com.stockly.stocklybackend.acceso.controller.AuthController.class,
                com.stockly.stocklybackend.acceso.controller.NegocioController.class,
                com.stockly.stocklybackend.acceso.controller.UsuarioNegocioController.class
        },
        properties = "app.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=")
@Import({SecurityConfig.class, JwtService.class})
class ApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private NegocioService negocioService;

    @MockitoBean
    private UsuarioNegocioService usuarioNegocioService;

    @MockitoBean
    private RolRepository rolRepository;

    @MockitoBean
    private UnidadMedidaRepository unidadMedidaRepository;

    @Test
    void endpointsDeRegistroYLoginSonPublicos() throws Exception {
        LoginResponse respuesta = new LoginResponse(42L, "Ana", "token-de-prueba",
                "Bearer", 3600, List.of());
        when(authService.registrar(any())).thenReturn(respuesta);
        when(authService.login(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","email":"ana@example.com","password":"password123","nombreNegocio":"Tienda"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {"accessToken":"token-de-prueba","tokenType":"Bearer","expiresIn":3600}
                        """));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"accessToken":"token-de-prueba","tokenType":"Bearer","expiresIn":3600}
                        """));
    }

    @Test
    void todosLosEndpointsDeNegocioExigenToken() throws Exception {
        mockMvc.perform(post("/api/negocios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Tienda"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/negocios/7/usuarios"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/negocios/7/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"vendedor@example.com","rol":"VENDEDOR"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/negocios/7/usuarios/9/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"rol":"ALMACENISTA"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/negocios/7/usuarios/9/desactivar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerEsPublicoSinToken() throws Exception {
        for (String ruta : List.of(
                "/swagger-ui/index.html",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config")) {
            mockMvc.perform(get(ruta))
                    .andExpect(result -> assertNotEquals(
                            UNAUTHORIZED.value(), result.getResponse().getStatus(), ruta));
        }
    }

    @Test
    void tokenInvalidoSeRechaza() throws Exception {
        mockMvc.perform(get("/api/negocios/7/usuarios")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenValidoProporcionaLaIdentidadDelSolicitanteATodasLasRutas() throws Exception {
        String token = jwtService.generarToken(42L, "ana@example.com");
        when(negocioService.crear(any(), any()))
                .thenReturn(new NegocioResumen(7L, "Tienda", NombreRol.PROPIETARIO));
        when(usuarioNegocioService.listar(7L, 42L)).thenReturn(List.of());

        mockMvc.perform(post("/api/negocios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Tienda"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/negocios/7/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(post("/api/negocios/7/usuarios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"vendedor@example.com","rol":"VENDEDOR"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/negocios/7/usuarios/9/rol")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"rol":"ALMACENISTA"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/negocios/7/usuarios/9/desactivar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(negocioService).crear(42L, new com.stockly.stocklybackend.acceso.dto.NegocioRequest(
                "Tienda", null, null));
        verify(usuarioNegocioService).listar(7L, 42L);
        verify(usuarioNegocioService).agregar(any(), any(), any());
        verify(usuarioNegocioService).cambiarRol(7L, 42L, 9L, NombreRol.ALMACENISTA);
        verify(usuarioNegocioService).desactivar(7L, 42L, 9L);
    }

    @Test
    void encoderDeContrasenasEsBcrypt() {
        String hash = passwordEncoder.encode("password123");

        org.junit.jupiter.api.Assertions.assertTrue(hash.startsWith("$2"));
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("password123", hash));
    }

    @Test
    void laApiPropagaElRechazoPorFaltaDeRolEnElNegocio() throws Exception {
        String token = jwtService.generarToken(42L, "ana@example.com");
        when(usuarioNegocioService.listar(7L, 42L))
                .thenThrow(new ResponseStatusException(FORBIDDEN));

        mockMvc.perform(get("/api/negocios/7/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}

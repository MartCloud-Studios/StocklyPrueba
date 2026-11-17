package com.stockly.stocklybackend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI stocklyOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Stockly API")
                        .description("""
                                API de Stockly para administrar acceso, catálogo, inventario y compras.
                                Los endpoints de negocio requieren un token JWT. Inicia sesión o
                                regístrate en el grupo Acceso y usa el botón Authorize para ingresar
                                el token recibido.
                                """)
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .tags(List.of(
                        new Tag().name("Autenticación")
                                .description("Registro de usuarios e inicio de sesión."),
                        new Tag().name("Negocios")
                                .description("Creación y administración de negocios."),
                        new Tag().name("Usuarios del negocio")
                                .description("Miembros y roles dentro de un negocio."),
                        new Tag().name("Categorías")
                                .description("Organización de productos por categoría."),
                        new Tag().name("Unidades de medida")
                                .description("Unidades de medida disponibles para productos."),
                        new Tag().name("Proveedores")
                                .description("Administración de proveedores del negocio."),
                        new Tag().name("Productos")
                                .description("Productos del catálogo."),
                        new Tag().name("Compras")
                                .description("Registro e historial de compras."),
                        new Tag().name("Inventario")
                                .description("Existencias y movimientos de inventario."),
                        new Tag().name("Ventas")
                                .description("Registro e historial de ventas.")));
    }

    @Bean
    GroupedOpenApi accesoApi() {
        return GroupedOpenApi.builder()
                .group("acceso")
                .packagesToScan("com.stockly.stocklybackend.acceso.controller")
                .build();
    }

    @Bean
    GroupedOpenApi catalogoApi() {
        return GroupedOpenApi.builder()
                .group("catalogo")
                .packagesToScan("com.stockly.stocklybackend.catalogo.controller")
                .build();
    }

    @Bean
    GroupedOpenApi comprasApi() {
        return GroupedOpenApi.builder()
                .group("compras")
                .packagesToScan("com.stockly.stocklybackend.compras.controller")
                .build();
    }

    @Bean
    GroupedOpenApi inventarioApi() {
        return GroupedOpenApi.builder()
                .group("inventario")
                .packagesToScan("com.stockly.stocklybackend.inventario.controller")
                .build();
    }

    @Bean
    GroupedOpenApi ventasApi() {
        return GroupedOpenApi.builder()
                .group("ventas")
                .packagesToScan("com.stockly.stocklybackend.ventas.controller")
                .build();
    }
}

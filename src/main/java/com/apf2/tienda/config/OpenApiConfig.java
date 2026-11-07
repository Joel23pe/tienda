package com.apf2.tienda.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DOCUMENTACIÓN CON SWAGGER / OPENAPI
 * Interfaz interactiva en /swagger-ui.html. Define el esquema de seguridad Bearer (JWT),
 * lo que habilita el botón "Authorize" para pegar el token y probar los endpoints protegidos.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI tiendaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Tienda")
                        .version("1.0")
                        .description("API REST de la tienda: productos, categorías y ventas "
                                + "(maestro-detalle). Autenticación con JWT: inicia sesión en "
                                + "/api/auth/login y pega el token en el botón Authorize."))
                // Exige el esquema Bearer en todos los endpoints documentados
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER))
                .components(new Components().addSecuritySchemes(ESQUEMA_BEARER,
                        new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
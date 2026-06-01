package com.frauddetection.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Swagger UI Documentation
 * 
 * Configures API metadata, security schemes, and JWT Bearer authentication
 * for the Fraud Detection System API documentation.
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI-Assisted Fraud Detection and Monitoring System")
                        .description(
                                "Secure fraud detection platform with JWT authentication, ML-assisted fraud analysis, "
                                        + "risk scoring, fraud alerts, and monitoring dashboard. "
                                        + "This API provides comprehensive endpoints for user authentication, transaction processing, "
                                        + "fraud alert management, and real-time monitoring through an admin dashboard. "
                                        + "All endpoints except authentication and API documentation require JWT Bearer token authentication.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Fraud Detection System Team")
                                .email("support@frauddetection.com")
                                .url("https://frauddetection.com")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "JWT Bearer token for API authentication. "
                                                        + "Obtain a token by calling /api/auth/login with valid credentials. "
                                                        + "Include the token in the Authorization header as: Bearer <token>")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}

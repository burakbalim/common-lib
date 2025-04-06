package com.thales.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for RESTful APIs
 */
@Configuration
public class OpenApiConfig {

    @Value("${springdoc.server.url:http://localhost:8080}")
    private String serverUrl;

    @Value("${springdoc.api.title:API}")
    private String apiTitle;

    @Value("${springdoc.api.description:RESTful API Service}")
    private String apiDescription;

    @Value("${springdoc.api.version:1.0.0}")
    private String apiVersion;

    @Value("${springdoc.api.contact.name:Support Team}")
    private String contactName;

    @Value("${springdoc.api.contact.email:support@example.com}")
    private String contactEmail;

    @Value("${springdoc.api.license.name:MIT License}")
    private String licenseName;

    @Value("${springdoc.api.license.url:https://opensource.org/licenses/MIT}")
    private String licenseUrl;

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url(serverUrl)
                .description("Local server");

        Contact contact = new Contact()
                .name(contactName)
                .email(contactEmail);

        License license = new License()
                .name(licenseName)
                .url(licenseUrl);

        Info info = new Info()
                .title(apiTitle)
                .version(apiVersion)
                .description(apiDescription)
                .contact(contact)
                .license(license);

        // JWT Authentication için Security Scheme tanımla
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Authorization header using the Bearer scheme. Example: 'Bearer {token}'");

        // Tüm API'ler için security requirement olarak ekle
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .openapi("3.0.1")
                .info(info)
                .servers(List.of(localServer))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
} 
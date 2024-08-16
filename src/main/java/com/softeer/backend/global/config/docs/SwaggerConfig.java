package com.softeer.backend.global.config.docs;

import com.softeer.backend.global.config.properties.JwtProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정 클래스
 */
@OpenAPIDefinition(
        info = @Info(title = "T라미숙해",
                description = "T라미숙해 api명세",
                version = "v1"),
        servers = {
                @Server(url = "https://softeer.shop"),
                @Server(url = "http://localhost:5000")
        }
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("T라미숙해 API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public OpenAPI getOpenApi() {
        Components components = new Components()
                .addSecuritySchemes("AccessToken", getJwtSecurityScheme())
                .addSecuritySchemes("RefreshToken", getJwtRefreshSecurityScheme());
        SecurityRequirement securityItem = new SecurityRequirement()
                .addList("AccessToken")
                .addList("RefreshToken");

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityItem);
    }

    private SecurityScheme getJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(jwtProperties.getAccessHeader());
    }

    private SecurityScheme getJwtRefreshSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(jwtProperties.getRefreshHeader());
    }
}

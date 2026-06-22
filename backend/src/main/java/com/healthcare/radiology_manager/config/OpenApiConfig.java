package com.healthcare.radiology_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Radiology Manager API")
                        .version("1.0")
                        .description("API documentation for the Radiology Manager application, simulating roles and equipment setup."))
                .addSecurityItem(new SecurityRequirement().addList("X-User-Role"))
                .components(new Components()
                        .addSecuritySchemes("X-User-Role", new SecurityScheme()
                                .in(SecurityScheme.In.HEADER)
                                .name("X-User-Role")
                                .description("Role-based access header. Specify ADMIN or USER.")));
    }
}

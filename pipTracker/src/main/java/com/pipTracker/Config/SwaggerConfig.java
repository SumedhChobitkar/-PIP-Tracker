
package com.pipTracker.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pipTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PipTracker API")
                        .description("API documentation for PipTracker application")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("QuantumSoft — Pip Tracker Team")
                                .email("support@quantumsoft.pip.com")
                                .url("https://quantumsoft.pip.com")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}



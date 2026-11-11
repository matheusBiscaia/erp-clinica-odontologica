package br.com.consultorio.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI configurarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP Clínica Odontológica")
                        .description(
                            "API para gerenciamento de pacientes, " +
                            "dentistas, agendamentos, prontuários e pagamentos"
                        )
                        .version("1.0.0")
                )
                .addSecurityItem(
                    new SecurityRequirement()
                        .addList(SECURITY_SCHEME)
                )
                .components(
                    new Components()
                        .addSecuritySchemes(
                            SECURITY_SCHEME,
                            new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}
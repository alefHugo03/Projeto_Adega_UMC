package api.servico.adega.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para a API de usuários.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adegaOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("API Adega")
                        .description("Documentação da API de gerenciamento de usuários da Adega")
                        .version("v1")
                        .contact(new Contact()
                                .name("Adega API")
                                .email("contato@adega.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}

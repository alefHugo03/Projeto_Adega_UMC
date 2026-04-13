package api.servico.adega.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração CORS para permitir requisições do frontend.
 * Necessária para que o frontend React (porta 5173) possa fazer requisições para a API.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permite CORS para todas as rotas da API
                        .allowedOrigins("http://localhost:5173", "https://localhost:5173") // Origem permitida: frontend local
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*") // Permite todos os headers
                        .allowCredentials(true); // Permite envio de credenciais (cookies, etc.)
            }
        };
    }
}
package org.example.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API") // Название вашего API
                        .version("1.0") // Версия API
                        .description("API documentation for the User Service application.") // Описание API
                        .termsOfService("http://swagger.io/terms/") // Условия использования (пример)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))); // Лицензия (пример)
    }
}
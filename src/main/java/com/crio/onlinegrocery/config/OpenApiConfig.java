package com.crio.onlinegrocery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI onlineGroceryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Grocery Ordering System API")
                        .description("RESTful API service using Spring Boot to manage an online grocery ordering system for customers, grocery items, and orders.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Grocery API Support")
                                .email("support@groceryapp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

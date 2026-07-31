package com.vamshi.expense_tracker.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Smart Expense Tracker API")

                        .description("REST API for managing personal expenses")

                        .version("1.0.0")

                        .contact(new Contact()

                                .name("Vamshi Vardhan Reddy")

                                .email("vamshivardhan987@gmail.com"))

                        .license(new License()

                                .name("MIT")))

                .externalDocs(new ExternalDocumentation()

                        .description("Project Repository")

                        .url("https://github.com/vamshi-987/expense-tracker-api"));
    }

}
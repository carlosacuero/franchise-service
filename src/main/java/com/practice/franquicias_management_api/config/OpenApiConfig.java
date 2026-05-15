package com.practice.franquicias_management_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	@Value("${server.port:8081}")
	private int serverPort;

	@Bean
	OpenAPI franquiciasOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("API Franquicias")
						.description("API reactiva para gestionar franquicias, sucursales y productos con stock.")
						.version("1.0.0")
						.contact(new Contact().name("Franquicias Management API")))
				.addServersItem(new Server()
						.url("http://localhost:" + serverPort)
						.description("Servidor local"));
	}
}

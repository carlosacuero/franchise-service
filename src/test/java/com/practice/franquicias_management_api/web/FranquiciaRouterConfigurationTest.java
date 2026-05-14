package com.practice.franquicias_management_api.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

class FranquiciaRouterConfigurationTest {

	@Test
	void franquiciaRoutes_registraRutas() {
		FranquiciaHandler handler = mock(FranquiciaHandler.class);
		RouterFunction<ServerResponse> routes = new FranquiciaRouterConfiguration().franquiciaRoutes(handler);
		assertThat(routes).isNotNull();
	}
}

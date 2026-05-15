package com.practice.franquicias_management_api.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.practice.franquicias_management_api.domain.Franquicia;
import com.practice.franquicias_management_api.dto.ProductoMaxStockPorSucursalResponse;
import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.exception.NotFoundException;
import com.practice.franquicias_management_api.service.FranquiciaService;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class FranquiciaHandlerTest {

	@Mock
	private FranquiciaService franquiciaService;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		FranquiciaHandler handler = new FranquiciaHandler(franquiciaService, validator);
		RouterFunction<ServerResponse> routes = new FranquiciaRouterConfiguration().franquiciaRoutes(handler);
		this.webTestClient = WebTestClient.bindToRouterFunction(routes)
				.handlerStrategies(HandlerStrategies.builder().exceptionHandler(new ApiExceptionHandler()).build())
				.build();
	}

	@Test
	void crearFranquicia_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("Demo").sucursales(List.of()).build();
		when(franquiciaService.crearFranquicia("Demo")).thenReturn(Mono.just(f));
		webTestClient.post()
				.uri("/api/v1/franquicias")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "Demo"))
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.id")
				.isEqualTo("f1")
				.jsonPath("$.nombre")
				.isEqualTo("Demo");
	}

	@Test
	void crearFranquicia_validacionDevuelve400() {
		webTestClient.post()
				.uri("/api/v1/franquicias")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", ""))
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	@Test
	void obtenerFranquicia_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.obtenerFranquicia("f1")).thenReturn(Mono.just(f));
		webTestClient.get().uri("/api/v1/franquicias/f1").exchange().expectStatus().isOk().expectBody().jsonPath("$.nombre").isEqualTo("F");
	}

	@Test
	void obtenerFranquicia_notFound() {
		when(franquiciaService.obtenerFranquicia("x")).thenReturn(Mono.error(new NotFoundException("nf")));
		webTestClient.get().uri("/api/v1/franquicias/x").exchange().expectStatus().isNotFound();
	}

	@Test
	void agregarSucursal_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.agregarSucursal("f1", "Suc")).thenReturn(Mono.just(f));
		webTestClient.post()
				.uri("/api/v1/franquicias/f1/sucursales")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "Suc"))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void agregarProducto_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.agregarProducto("f1", "s1", "P", 2)).thenReturn(Mono.just(f));
		webTestClient.post()
				.uri("/api/v1/franquicias/f1/sucursales/s1/productos")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "P", "stock", 2))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void eliminarProducto_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.eliminarProducto("f1", "s1", "p1")).thenReturn(Mono.just(f));
		webTestClient.delete().uri("/api/v1/franquicias/f1/sucursales/s1/productos/p1").exchange().expectStatus().isOk();
	}

	@Test
	void actualizarStock_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.actualizarStock("f1", "s1", "p1", 5)).thenReturn(Mono.just(f));
		webTestClient.patch()
				.uri("/api/v1/franquicias/f1/sucursales/s1/productos/p1/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("stock", 5))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void productosMayorStockPorSucursal_ok() {
		var r = new ProductoMaxStockPorSucursalResponse("s1", "S", "p1", "P", 9);
		when(franquiciaService.productosConMayorStockPorSucursal("f1")).thenReturn(Mono.just(List.of(r)));
		webTestClient.get()
				.uri("/api/v1/franquicias/f1/productos/mayor-stock-por-sucursal")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$[0].stock")
				.isEqualTo(9);
	}

	@Test
	void actualizarNombreFranquicia_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("N").sucursales(List.of()).build();
		when(franquiciaService.actualizarNombreFranquicia("f1", "N")).thenReturn(Mono.just(f));
		webTestClient.patch()
				.uri("/api/v1/franquicias/f1/nombre")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "N"))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void actualizarNombreSucursal_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.actualizarNombreSucursal("f1", "s1", "SN")).thenReturn(Mono.just(f));
		webTestClient.patch()
				.uri("/api/v1/franquicias/f1/sucursales/s1/nombre")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "SN"))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void actualizarNombreProducto_ok() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(List.of()).build();
		when(franquiciaService.actualizarNombreProducto("f1", "s1", "p1", "PN")).thenReturn(Mono.just(f));
		webTestClient.patch()
				.uri("/api/v1/franquicias/f1/sucursales/s1/productos/p1/nombre")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "PN"))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void servicioBadRequest_propaga400() {
		when(franquiciaService.crearFranquicia(anyString()))
				.thenReturn(Mono.error(new BadRequestException("fallo")));
		webTestClient.post()
				.uri("/api/v1/franquicias")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("nombre", "ok"))
				.exchange()
				.expectStatus()
				.isBadRequest();
	}
}

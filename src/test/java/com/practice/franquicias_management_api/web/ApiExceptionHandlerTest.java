package com.practice.franquicias_management_api.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.practice.franquicias_management_api.dto.CrearFranquiciaRequest;
import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.exception.NotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import reactor.test.StepVerifier;

class ApiExceptionHandlerTest {

	private final ApiExceptionHandler handler = new ApiExceptionHandler();
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void handle_notFound_escribeJson404() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		StepVerifier.create(handler.handle(exchange, new NotFoundException("no existe")))
				.verifyComplete();
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(exchange.getResponse().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
				.assertNext(buf -> {
					byte[] bytes = new byte[buf.readableByteCount()];
					buf.read(bytes);
					assertThat(new String(bytes)).contains("no existe");
				})
				.verifyComplete();
	}

	@Test
	void handle_badRequest_escribeJson400() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		StepVerifier.create(handler.handle(exchange, new BadRequestException("mal")))
				.verifyComplete();
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void handle_otraExcepcion_noModificaRespuesta() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		StepVerifier.create(handler.handle(exchange, new IllegalStateException("x")))
				.verifyComplete();
		assertThat(exchange.getResponse().getStatusCode()).isNull();
	}

	@Test
	void handle_notFound_mensajeNull_escapaJson() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		StepVerifier.create(handler.handle(exchange, new NotFoundException(null)))
				.verifyComplete();
		StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
				.assertNext(buf -> {
					byte[] bytes = new byte[buf.readableByteCount()];
					buf.read(bytes);
					assertThat(new String(bytes)).isEqualTo("{\"mensaje\":\"\"}");
				})
				.verifyComplete();
	}

	@Test
	void handle_badRequest_escapaComillasEnJson() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		StepVerifier.create(handler.handle(exchange, new BadRequestException("say \"x\"")))
				.verifyComplete();
		StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
				.assertNext(buf -> {
					byte[] bytes = new byte[buf.readableByteCount()];
					buf.read(bytes);
					assertThat(new String(bytes)).contains("\\\"x\\\"");
				})
				.verifyComplete();
	}

	@Test
	void mensajeViolaciones_concatenaMensajes() {
		Set<ConstraintViolation<CrearFranquiciaRequest>> violations = validator.validate(new CrearFranquiciaRequest(""));
		assertThat(violations).isNotEmpty();
		String msg = ApiExceptionHandler.mensajeViolaciones(violations);
		assertThat(msg).contains("obligatorio");
	}
}

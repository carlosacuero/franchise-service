package com.practice.franquicias_management_api.web;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.exception.NotFoundException;

import jakarta.validation.ConstraintViolation;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class ApiExceptionHandler implements WebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		if (ex instanceof NotFoundException nfe) {
			return json(exchange, HttpStatus.NOT_FOUND, nfe.getMessage());
		}
		if (ex instanceof BadRequestException bre) {
			return json(exchange, HttpStatus.BAD_REQUEST, bre.getMessage());
		}
		return Mono.empty();
	}

	private static Mono<Void> json(ServerWebExchange exchange, HttpStatus status, String mensaje) {
		exchange.getResponse().setStatusCode(status);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		String escaped = mensaje == null ? "" : mensaje.replace("\\", "\\\\").replace("\"", "\\\"");
		byte[] bytes = ("{\"mensaje\":\"" + escaped + "\"}").getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
		return exchange.getResponse().writeWith(Mono.just(buffer));
	}

	public static String mensajeViolaciones(Set<? extends ConstraintViolation<?>> violations) {
		return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
	}
}

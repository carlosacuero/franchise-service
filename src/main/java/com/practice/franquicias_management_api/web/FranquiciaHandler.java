package com.practice.franquicias_management_api.web;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.practice.franquicias_management_api.dto.ActualizarNombreRequest;
import com.practice.franquicias_management_api.dto.ActualizarStockRequest;
import com.practice.franquicias_management_api.dto.CrearFranquiciaRequest;
import com.practice.franquicias_management_api.dto.CrearProductoRequest;
import com.practice.franquicias_management_api.dto.CrearSucursalRequest;
import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.service.FranquiciaService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranquiciaHandler {

	private final FranquiciaService franquiciaService;
	private final Validator validator;

	public Mono<ServerResponse> crearFranquicia(ServerRequest request) {
		return request.bodyToMono(CrearFranquiciaRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.crearFranquicia(body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> obtenerFranquicia(ServerRequest request) {
		String id = request.pathVariable("franquiciaId");
		return franquiciaService.obtenerFranquicia(id)
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> agregarSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		return request.bodyToMono(CrearSucursalRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.agregarSucursal(franquiciaId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> agregarProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		return request.bodyToMono(CrearProductoRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.agregarProducto(franquiciaId, sucursalId, body.nombre(), body.stock()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> eliminarProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		return franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId)
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> actualizarStock(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		return request.bodyToMono(ActualizarStockRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarStock(franquiciaId, sucursalId, productoId, body.stock()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> productosMayorStockPorSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		return franquiciaService.productosConMayorStockPorSucursal(franquiciaId)
				.flatMap(lista -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(lista));
	}

	public Mono<ServerResponse> actualizarNombreFranquicia(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreFranquicia(franquiciaId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> actualizarNombreSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	public Mono<ServerResponse> actualizarNombreProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreProducto(franquiciaId, sucursalId, productoId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
	}

	private <T> Mono<T> validar(T object) {
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		if (!violations.isEmpty()) {
			return Mono.error(new BadRequestException(ApiExceptionHandler.mensajeViolaciones(violations)));
		}
		return Mono.just(object);
	}
}

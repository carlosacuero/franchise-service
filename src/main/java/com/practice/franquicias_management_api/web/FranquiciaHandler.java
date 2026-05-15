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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranquiciaHandler {

	private final FranquiciaService franquiciaService;
	private final Validator validator;

	public Mono<ServerResponse> crearFranquicia(ServerRequest request) {
		log.debug("Handler crearFranquicia");
		return request.bodyToMono(CrearFranquiciaRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.crearFranquicia(body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en crearFranquicia: {}", e.getMessage()));
	}

	public Mono<ServerResponse> obtenerFranquicia(ServerRequest request) {
		String id = request.pathVariable("franquiciaId");
		log.debug("Handler obtenerFranquicia: franquiciaId={}", id);
		return franquiciaService.obtenerFranquicia(id)
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en obtenerFranquicia id={}: {}", id, e.getMessage()));
	}

	public Mono<ServerResponse> agregarSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		log.debug("Handler agregarSucursal: franquiciaId={}", franquiciaId);
		return request.bodyToMono(CrearSucursalRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.agregarSucursal(franquiciaId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en agregarSucursal franquiciaId={}: {}", franquiciaId, e.getMessage()));
	}

	public Mono<ServerResponse> agregarProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		log.debug("Handler agregarProducto: franquiciaId={}, sucursalId={}", franquiciaId, sucursalId);
		return request.bodyToMono(CrearProductoRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.agregarProducto(franquiciaId, sucursalId, body.nombre(), body.stock()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en agregarProducto: {}", e.getMessage()));
	}

	public Mono<ServerResponse> eliminarProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		log.debug("Handler eliminarProducto: franquiciaId={}, sucursalId={}, productoId={}",
				franquiciaId, sucursalId, productoId);
		return franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId)
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en eliminarProducto: {}", e.getMessage()));
	}

	public Mono<ServerResponse> actualizarStock(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		log.debug("Handler actualizarStock: franquiciaId={}, sucursalId={}, productoId={}",
				franquiciaId, sucursalId, productoId);
		return request.bodyToMono(ActualizarStockRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarStock(franquiciaId, sucursalId, productoId, body.stock()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en actualizarStock: {}", e.getMessage()));
	}

	public Mono<ServerResponse> productosMayorStockPorSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		log.debug("Handler productosMayorStockPorSucursal: franquiciaId={}", franquiciaId);
		return franquiciaService.productosConMayorStockPorSucursal(franquiciaId)
				.flatMap(lista -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(lista))
				.doOnError(e -> log.error("Error en productosMayorStockPorSucursal: {}", e.getMessage()));
	}

	public Mono<ServerResponse> actualizarNombreFranquicia(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		log.debug("Handler actualizarNombreFranquicia: franquiciaId={}", franquiciaId);
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreFranquicia(franquiciaId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en actualizarNombreFranquicia: {}", e.getMessage()));
	}

	public Mono<ServerResponse> actualizarNombreSucursal(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		log.debug("Handler actualizarNombreSucursal: franquiciaId={}, sucursalId={}", franquiciaId, sucursalId);
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en actualizarNombreSucursal: {}", e.getMessage()));
	}

	public Mono<ServerResponse> actualizarNombreProducto(ServerRequest request) {
		String franquiciaId = request.pathVariable("franquiciaId");
		String sucursalId = request.pathVariable("sucursalId");
		String productoId = request.pathVariable("productoId");
		log.debug("Handler actualizarNombreProducto: franquiciaId={}, sucursalId={}, productoId={}",
				franquiciaId, sucursalId, productoId);
		return request.bodyToMono(ActualizarNombreRequest.class)
				.flatMap(this::validar)
				.flatMap(body -> franquiciaService.actualizarNombreProducto(franquiciaId, sucursalId, productoId, body.nombre()))
				.flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f))
				.doOnError(e -> log.error("Error en actualizarNombreProducto: {}", e.getMessage()));
	}

	private <T> Mono<T> validar(T object) {
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		if (!violations.isEmpty()) {
			String mensaje = ApiExceptionHandler.mensajeViolaciones(violations);
			log.warn("Validación fallida: {}", mensaje);
			return Mono.error(new BadRequestException(mensaje));
		}
		return Mono.just(object);
	}
}

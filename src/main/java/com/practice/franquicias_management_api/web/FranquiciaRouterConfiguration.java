package com.practice.franquicias_management_api.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FranquiciaRouterConfiguration {

	@Bean
	RouterFunction<ServerResponse> franquiciaRoutes(FranquiciaHandler handler) {
		return route(POST("/api/v1/franquicias"), handler::crearFranquicia)
				.andRoute(GET("/api/v1/franquicias/{franquiciaId}"), handler::obtenerFranquicia)
				.andRoute(POST("/api/v1/franquicias/{franquiciaId}/sucursales"), handler::agregarSucursal)
				.andRoute(POST("/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos"), handler::agregarProducto)
				.andRoute(
						DELETE("/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}"),
						handler::eliminarProducto)
				.andRoute(
						PATCH("/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock"),
						handler::actualizarStock)
				.andRoute(
						GET("/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal"),
						handler::productosMayorStockPorSucursal)
				.andRoute(PATCH("/api/v1/franquicias/{franquiciaId}/nombre"), handler::actualizarNombreFranquicia)
				.andRoute(
						PATCH("/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre"),
						handler::actualizarNombreSucursal)
				.andRoute(
						PATCH("/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre"),
						handler::actualizarNombreProducto);
	}
}

package com.practice.franquicias_management_api.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.practice.franquicias_management_api.domain.Franquicia;
import com.practice.franquicias_management_api.dto.ActualizarNombreRequest;
import com.practice.franquicias_management_api.dto.ActualizarStockRequest;
import com.practice.franquicias_management_api.dto.CrearFranquiciaRequest;
import com.practice.franquicias_management_api.dto.CrearProductoRequest;
import com.practice.franquicias_management_api.dto.CrearSucursalRequest;
import com.practice.franquicias_management_api.dto.ErrorResponse;
import com.practice.franquicias_management_api.dto.ProductoMaxStockPorSucursalResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class FranquiciaRouterConfiguration {

	@Bean
	@RouterOperations({
			@RouterOperation(
					path = "/api/v1/franquicias",
					method = RequestMethod.POST,
					beanClass = FranquiciaHandler.class,
					beanMethod = "crearFranquicia",
					operation = @Operation(
							operationId = "crearFranquicia",
							summary = "Crear franquicia",
							description = "Registra una nueva franquicia con el nombre indicado.",
							tags = { "Franquicias" },
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = CrearFranquiciaRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Franquicia creada",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}",
					method = RequestMethod.GET,
					beanClass = FranquiciaHandler.class,
					beanMethod = "obtenerFranquicia",
					operation = @Operation(
							operationId = "obtenerFranquicia",
							summary = "Obtener franquicia",
							description = "Devuelve la franquicia con sus sucursales y productos.",
							tags = { "Franquicias" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											description = "Identificador de la franquicia",
											schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
							},
							responses = {
									@ApiResponse(responseCode = "200", description = "Franquicia encontrada",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales",
					method = RequestMethod.POST,
					beanClass = FranquiciaHandler.class,
					beanMethod = "agregarSucursal",
					operation = @Operation(
							operationId = "agregarSucursal",
							summary = "Agregar sucursal",
							description = "Añade una sucursal a la franquicia indicada.",
							tags = { "Sucursales" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = CrearSucursalRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Sucursal agregada",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos",
					method = RequestMethod.POST,
					beanClass = FranquiciaHandler.class,
					beanMethod = "agregarProducto",
					operation = @Operation(
							operationId = "agregarProducto",
							summary = "Agregar producto",
							description = "Registra un producto con stock en la sucursal indicada.",
							tags = { "Productos" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "sucursalId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = CrearProductoRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Producto agregado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}",
					method = RequestMethod.DELETE,
					beanClass = FranquiciaHandler.class,
					beanMethod = "eliminarProducto",
					operation = @Operation(
							operationId = "eliminarProducto",
							summary = "Eliminar producto",
							description = "Elimina un producto de la sucursal indicada.",
							tags = { "Productos" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "sucursalId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "productoId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							responses = {
									@ApiResponse(responseCode = "200", description = "Producto eliminado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "404", description = "Recurso no encontrado",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock",
					method = RequestMethod.PATCH,
					beanClass = FranquiciaHandler.class,
					beanMethod = "actualizarStock",
					operation = @Operation(
							operationId = "actualizarStock",
							summary = "Actualizar stock",
							description = "Actualiza el stock de un producto en una sucursal.",
							tags = { "Productos" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "sucursalId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "productoId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = ActualizarStockRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Stock actualizado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Stock inválido",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Recurso no encontrado",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal",
					method = RequestMethod.GET,
					beanClass = FranquiciaHandler.class,
					beanMethod = "productosMayorStockPorSucursal",
					operation = @Operation(
							operationId = "productosMayorStockPorSucursal",
							summary = "Producto con mayor stock por sucursal",
							description = "Por cada sucursal con productos, devuelve el de mayor stock (empate por nombre).",
							tags = { "Consultas" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							responses = {
									@ApiResponse(responseCode = "200", description = "Listado obtenido",
											content = @Content(array = @ArraySchema(
													schema = @Schema(implementation = ProductoMaxStockPorSucursalResponse.class)))),
									@ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/nombre",
					method = RequestMethod.PATCH,
					beanClass = FranquiciaHandler.class,
					beanMethod = "actualizarNombreFranquicia",
					operation = @Operation(
							operationId = "actualizarNombreFranquicia",
							summary = "Actualizar nombre de franquicia",
							tags = { "Franquicias" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = ActualizarNombreRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Nombre actualizado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre",
					method = RequestMethod.PATCH,
					beanClass = FranquiciaHandler.class,
					beanMethod = "actualizarNombreSucursal",
					operation = @Operation(
							operationId = "actualizarNombreSucursal",
							summary = "Actualizar nombre de sucursal",
							tags = { "Sucursales" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "sucursalId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = ActualizarNombreRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Nombre actualizado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Recurso no encontrado",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							})),
			@RouterOperation(
					path = "/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre",
					method = RequestMethod.PATCH,
					beanClass = FranquiciaHandler.class,
					beanMethod = "actualizarNombreProducto",
					operation = @Operation(
							operationId = "actualizarNombreProducto",
							summary = "Actualizar nombre de producto",
							tags = { "Productos" },
							parameters = {
									@Parameter(name = "franquiciaId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "sucursalId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string")),
									@Parameter(name = "productoId", in = ParameterIn.PATH, required = true,
											schema = @Schema(type = "string"))
							},
							requestBody = @RequestBody(
									required = true,
									content = @Content(schema = @Schema(implementation = ActualizarNombreRequest.class))),
							responses = {
									@ApiResponse(responseCode = "200", description = "Nombre actualizado",
											content = @Content(schema = @Schema(implementation = Franquicia.class))),
									@ApiResponse(responseCode = "400", description = "Datos inválidos",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
									@ApiResponse(responseCode = "404", description = "Recurso no encontrado",
											content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
							}))
	})
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

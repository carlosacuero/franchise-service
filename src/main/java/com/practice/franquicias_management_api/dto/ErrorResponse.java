package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Mensaje de error devuelto por la API")
public record ErrorResponse(
		@Schema(description = "Descripción del error", example = "El nombre es obligatorio") String mensaje) {
}

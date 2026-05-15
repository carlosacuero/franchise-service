package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ActualizarNombreRequest", description = "Nuevo nombre para franquicia, sucursal o producto")
public record ActualizarNombreRequest(
		@Schema(description = "Nuevo nombre", example = "Franquicia Actualizada", requiredMode = RequiredMode.REQUIRED)
		@NotBlank(message = "El nombre es obligatorio") String nombre) {
}

package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CrearSucursalRequest", description = "Datos para agregar una sucursal a una franquicia")
public record CrearSucursalRequest(
		@Schema(description = "Nombre de la sucursal", example = "Sucursal Centro", requiredMode = RequiredMode.REQUIRED)
		@NotBlank(message = "El nombre es obligatorio") String nombre) {
}

package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CrearFranquiciaRequest", description = "Datos para registrar una franquicia")
public record CrearFranquiciaRequest(
		@Schema(description = "Nombre de la franquicia", example = "Franquicia Norte", requiredMode = RequiredMode.REQUIRED)
		@NotBlank(message = "El nombre es obligatorio") String nombre) {
}

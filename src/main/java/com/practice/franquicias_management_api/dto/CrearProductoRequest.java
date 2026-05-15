package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CrearProductoRequest", description = "Datos para registrar un producto en una sucursal")
public record CrearProductoRequest(
		@Schema(description = "Nombre del producto", example = "Producto A", requiredMode = RequiredMode.REQUIRED)
		@NotBlank(message = "El nombre es obligatorio") String nombre,
		@Schema(description = "Cantidad en inventario", example = "10", minimum = "0", requiredMode = RequiredMode.REQUIRED)
		@NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) {
}

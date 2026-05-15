package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarStockRequest", description = "Nuevo valor de stock para un producto")
public record ActualizarStockRequest(
		@Schema(description = "Stock actualizado", example = "25", minimum = "0", requiredMode = RequiredMode.REQUIRED)
		@NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) {
}

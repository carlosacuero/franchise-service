package com.practice.franquicias_management_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductoMaxStockPorSucursalResponse",
		description = "Producto con mayor stock en una sucursal, con datos de contexto")
public record ProductoMaxStockPorSucursalResponse(
		@Schema(description = "ID de la sucursal", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890") String sucursalId,
		@Schema(description = "Nombre de la sucursal", example = "Sucursal Centro") String sucursalNombre,
		@Schema(description = "ID del producto", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901") String productoId,
		@Schema(description = "Nombre del producto", example = "Producto B") String productoNombre,
		@Schema(description = "Stock del producto", example = "50") int stock) {
}

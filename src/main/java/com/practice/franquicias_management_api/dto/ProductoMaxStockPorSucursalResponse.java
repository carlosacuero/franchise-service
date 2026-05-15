package com.practice.franquicias_management_api.dto;

public record ProductoMaxStockPorSucursalResponse(
		String sucursalId,
		String sucursalNombre,
		String productoId,
		String productoNombre,
		int stock
) {
}

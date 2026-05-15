package com.practice.franquicias_management_api.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Producto", description = "Producto con stock en una sucursal")
public class Producto {

	@Schema(description = "Identificador del producto", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
	private String id;
	@Schema(description = "Nombre del producto", example = "Producto A")
	private String nombre;
	@Schema(description = "Cantidad en inventario", example = "10", minimum = "0")
	private int stock;
}

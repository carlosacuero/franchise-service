package com.practice.franquicias_management_api.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Sucursal", description = "Sucursal de una franquicia")
public class Sucursal {

	@Schema(description = "Identificador de la sucursal", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
	private String id;
	@Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
	private String nombre;
	@Builder.Default
	@Schema(description = "Productos disponibles en la sucursal")
	private List<Producto> productos = new ArrayList<>();
}

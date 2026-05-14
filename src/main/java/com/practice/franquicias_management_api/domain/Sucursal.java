package com.practice.franquicias_management_api.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sucursal {

	private String id;
	private String nombre;
	@Builder.Default
	private List<Producto> productos = new ArrayList<>();
}

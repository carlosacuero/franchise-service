package com.practice.franquicias_management_api.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franquicias")
@Schema(name = "Franquicia", description = "Franquicia con sucursales y productos anidados")
public class Franquicia {

	@Id
	@Schema(description = "Identificador de la franquicia", example = "507f1f77bcf86cd799439011")
	private String id;
	@Schema(description = "Nombre de la franquicia", example = "Franquicia Demo")
	private String nombre;
	@Builder.Default
	@Schema(description = "Sucursales pertenecientes a la franquicia")
	private List<Sucursal> sucursales = new ArrayList<>();
}

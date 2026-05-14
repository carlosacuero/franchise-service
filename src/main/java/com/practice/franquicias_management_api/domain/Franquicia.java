package com.practice.franquicias_management_api.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franquicias")
public class Franquicia {

	@Id
	private String id;
	private String nombre;
	@Builder.Default
	private List<Sucursal> sucursales = new ArrayList<>();
}

package com.practice.franquicias_management_api.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarNombreRequest(@NotBlank(message = "El nombre es obligatorio") String nombre) {
}

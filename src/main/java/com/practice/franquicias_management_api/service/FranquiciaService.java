package com.practice.franquicias_management_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.practice.franquicias_management_api.domain.Franquicia;
import com.practice.franquicias_management_api.domain.Producto;
import com.practice.franquicias_management_api.domain.Sucursal;
import com.practice.franquicias_management_api.dto.ProductoMaxStockPorSucursalResponse;
import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.exception.NotFoundException;
import com.practice.franquicias_management_api.repository.FranquiciaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranquiciaService {

	private static final Comparator<Producto> POR_STOCK_LUEGO_NOMBRE = Comparator
			.comparingInt(Producto::getStock)
			.thenComparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER);

	private final FranquiciaRepository repository;

	public Mono<Franquicia> crearFranquicia(String nombre) {
		requireNombre(nombre);
		Franquicia f = Franquicia.builder()
				.nombre(nombre.trim())
				.sucursales(new ArrayList<>())
				.build();
		return repository.save(f);
	}

	public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombre) {
		requireNombre(nombre);
		return obtenerFranquicia(franquiciaId).map(f -> {
			List<Sucursal> copia = new ArrayList<>(Optional.ofNullable(f.getSucursales()).orElseGet(ArrayList::new));
			copia.add(Sucursal.builder()
					.id(UUID.randomUUID().toString())
					.nombre(nombre.trim())
					.productos(new ArrayList<>())
					.build());
			f.setSucursales(copia);
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombre, int stock) {
		requireNombre(nombre);
		if (stock < 0) {
			return Mono.error(new BadRequestException("El stock no puede ser negativo"));
		}
		return obtenerFranquicia(franquiciaId).map(f -> {
			Sucursal s = buscarSucursal(f, sucursalId);
			List<Producto> copia = new ArrayList<>(Optional.ofNullable(s.getProductos()).orElseGet(ArrayList::new));
			copia.add(Producto.builder()
					.id(UUID.randomUUID().toString())
					.nombre(nombre.trim())
					.stock(stock)
					.build());
			s.setProductos(copia);
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
		return obtenerFranquicia(franquiciaId).map(f -> {
			Sucursal s = buscarSucursal(f, sucursalId);
			List<Producto> filtrados = Optional.ofNullable(s.getProductos()).orElseGet(ArrayList::new).stream()
					.filter(p -> !productoId.equals(p.getId()))
					.collect(Collectors.toCollection(ArrayList::new));
			if (filtrados.size() == Optional.ofNullable(s.getProductos()).map(List::size).orElse(0)) {
				throw new NotFoundException("Producto no encontrado en la sucursal: " + productoId);
			}
			s.setProductos(filtrados);
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> actualizarStock(String franquiciaId, String sucursalId, String productoId, int stock) {
		if (stock < 0) {
			return Mono.error(new BadRequestException("El stock no puede ser negativo"));
		}
		return obtenerFranquicia(franquiciaId).map(f -> {
			Producto p = buscarProducto(f, sucursalId, productoId);
			p.setStock(stock);
			return f;
		}).flatMap(repository::save);
	}

	public Mono<List<ProductoMaxStockPorSucursalResponse>> productosConMayorStockPorSucursal(String franquiciaId) {
		return obtenerFranquicia(franquiciaId).map(f -> Optional.ofNullable(f.getSucursales()).orElseGet(ArrayList::new).stream()
				.map(s -> productoConMayorStockEnSucursal(s)
						.map(p -> new ProductoMaxStockPorSucursalResponse(
								s.getId(),
								s.getNombre(),
								p.getId(),
								p.getNombre(),
								p.getStock())))
				.flatMap(Optional::stream)
				.toList());
	}

	public Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre) {
		requireNombre(nuevoNombre);
		return obtenerFranquicia(franquiciaId).map(f -> {
			f.setNombre(nuevoNombre.trim());
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre) {
		requireNombre(nuevoNombre);
		return obtenerFranquicia(franquiciaId).map(f -> {
			Sucursal s = buscarSucursal(f, sucursalId);
			s.setNombre(nuevoNombre.trim());
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> actualizarNombreProducto(
			String franquiciaId, String sucursalId, String productoId, String nuevoNombre) {
		requireNombre(nuevoNombre);
		return obtenerFranquicia(franquiciaId).map(f -> {
			Producto p = buscarProducto(f, sucursalId, productoId);
			p.setNombre(nuevoNombre.trim());
			return f;
		}).flatMap(repository::save);
	}

	public Mono<Franquicia> obtenerFranquicia(String id) {
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada: " + id)));
	}

	private static void requireNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new BadRequestException("El nombre es obligatorio");
		}
	}

	private static Sucursal buscarSucursal(Franquicia f, String sucursalId) {
		return Optional.ofNullable(f.getSucursales()).orElseGet(ArrayList::new).stream()
				.filter(s -> sucursalId.equals(s.getId()))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Sucursal no encontrada: " + sucursalId));
	}

	private static Producto buscarProducto(Franquicia f, String sucursalId, String productoId) {
		Sucursal s = buscarSucursal(f, sucursalId);
		return Optional.ofNullable(s.getProductos()).orElseGet(ArrayList::new).stream()
				.filter(p -> productoId.equals(p.getId()))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productoId));
	}

	private static Optional<Producto> productoConMayorStockEnSucursal(Sucursal s) {
		List<Producto> lista = Optional.ofNullable(s.getProductos()).orElseGet(ArrayList::new);
		if (lista.isEmpty()) {
			return Optional.empty();
		}
		return lista.stream().max(POR_STOCK_LUEGO_NOMBRE);
	}
}

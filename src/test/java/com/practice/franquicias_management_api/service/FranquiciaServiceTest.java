package com.practice.franquicias_management_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.practice.franquicias_management_api.domain.Franquicia;
import com.practice.franquicias_management_api.domain.Producto;
import com.practice.franquicias_management_api.domain.Sucursal;
import com.practice.franquicias_management_api.dto.ProductoMaxStockPorSucursalResponse;
import com.practice.franquicias_management_api.exception.BadRequestException;
import com.practice.franquicias_management_api.exception.NotFoundException;
import com.practice.franquicias_management_api.repository.FranquiciaRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FranquiciaServiceTest {

	@Mock
	private FranquiciaRepository repository;

	@InjectMocks
	private FranquiciaService service;

	@BeforeEach
	void setUp() {
		lenient().when(repository.save(any(Franquicia.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
	}

	@Test
	void crearFranquicia_guardaConNombreRecortado() {
		StepVerifier.create(service.crearFranquicia("  Demo  "))
				.assertNext(f -> {
					assertThat(f.getNombre()).isEqualTo("Demo");
					assertThat(f.getSucursales()).isEmpty();
				})
				.verifyComplete();
		verify(repository).save(any(Franquicia.class));
	}

	@Test
	void crearFranquicia_nombreVacio_lanzaBadRequest() {
		BadRequestException ex = assertThrows(BadRequestException.class, () -> service.crearFranquicia("   "));
		assertThat(ex.getMessage()).contains("obligatorio");
	}

	@Test
	void crearFranquicia_nombreNull_lanzaBadRequest() {
		BadRequestException ex = assertThrows(BadRequestException.class, () -> service.crearFranquicia(null));
		assertThat(ex.getMessage()).contains("obligatorio");
	}

	@Test
	void obtenerFranquicia_noExiste_lanzaNotFound() {
		when(repository.findById("x")).thenReturn(Mono.empty());
		StepVerifier.create(service.obtenerFranquicia("x"))
				.expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void agregarSucursal_cuandoSucursalesNull_agregaCorrectamente() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(null).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.agregarSucursal("f1", " Centro "))
				.assertNext(actual -> {
					assertThat(actual.getSucursales()).hasSize(1);
					assertThat(actual.getSucursales().get(0).getNombre()).isEqualTo("Centro");
					assertThat(actual.getSucursales().get(0).getProductos()).isEmpty();
					assertThat(actual.getSucursales().get(0).getId()).isNotBlank();
				})
				.verifyComplete();
	}

	@Test
	void agregarSucursal_nombreInvalido_lanzaBadRequest() {
		BadRequestException ex = assertThrows(BadRequestException.class, () -> service.agregarSucursal("f1", ""));
		assertThat(ex.getMessage()).contains("obligatorio");
	}

	@Test
	void agregarProducto_stockNegativo_lanzaBadRequest() {
		StepVerifier.create(service.agregarProducto("f1", "s1", "P", -1))
				.expectError(BadRequestException.class)
				.verify();
	}

	@Test
	void agregarProducto_cuandoProductosNull_agregaProducto() {
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(null).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.agregarProducto("f1", "s1", " Item ", 3))
				.assertNext(actual -> {
					Sucursal suc = actual.getSucursales().get(0);
					assertThat(suc.getProductos()).hasSize(1);
					assertThat(suc.getProductos().get(0).getNombre()).isEqualTo("Item");
					assertThat(suc.getProductos().get(0).getStock()).isEqualTo(3);
				})
				.verifyComplete();
	}

	@Test
	void agregarProducto_sucursalInexistente_lanzaNotFound() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>()).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.agregarProducto("f1", "no", "P", 1))
				.expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void eliminarProducto_eliminaYGuarda() {
		Producto p = Producto.builder().id("p1").nombre("A").stock(1).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.eliminarProducto("f1", "s1", "p1"))
				.assertNext(actual -> assertThat(actual.getSucursales().get(0).getProductos()).isEmpty())
				.verifyComplete();
	}

	@Test
	void eliminarProducto_idInexistente_lanzaNotFound() {
		Producto p = Producto.builder().id("p1").nombre("A").stock(1).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.eliminarProducto("f1", "s1", "otro"))
				.expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void eliminarProducto_listaProductosNull_trataComoVacia() {
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(null).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.eliminarProducto("f1", "s1", "p1"))
				.expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void actualizarStock_negativo_lanzaBadRequest() {
		StepVerifier.create(service.actualizarStock("f1", "s1", "p1", -2))
				.expectError(BadRequestException.class)
				.verify();
	}

	@Test
	void actualizarStock_actualizaYGuarda() {
		Producto p = Producto.builder().id("p1").nombre("A").stock(1).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.actualizarStock("f1", "s1", "p1", 9))
				.assertNext(actual -> assertThat(actual.getSucursales().get(0).getProductos().get(0).getStock()).isEqualTo(9))
				.verifyComplete();
	}

	@Test
	void actualizarStock_productoInexistente_lanzaNotFound() {
		Producto p = Producto.builder().id("p1").nombre("A").stock(1).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.actualizarStock("f1", "s1", "x", 1))
				.expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void productosConMayorStockPorSucursal_emparejaStockDesempataPorNombre() {
		Producto a = Producto.builder().id("pa").nombre("alpha").stock(10).build();
		Producto b = Producto.builder().id("pb").nombre("bravo").stock(10).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(a, b))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.productosConMayorStockPorSucursal("f1"))
				.assertNext(lista -> {
					assertThat(lista).hasSize(1);
					ProductoMaxStockPorSucursalResponse r = lista.get(0);
					assertThat(r.productoId()).isEqualTo("pb");
					assertThat(r.productoNombre()).isEqualTo("bravo");
					assertThat(r.stock()).isEqualTo(10);
				})
				.verifyComplete();
	}

	@Test
	void productosConMayorStockPorSucursal_omiteSucursalSinProductos() {
		Sucursal vacia = Sucursal.builder().id("s0").nombre("V").productos(List.of()).build();
		Producto p = Producto.builder().id("p1").nombre("X").stock(2).build();
		Sucursal con = Sucursal.builder().id("s1").nombre("C").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(vacia, con))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.productosConMayorStockPorSucursal("f1"))
				.assertNext(lista -> assertThat(lista).hasSize(1))
				.verifyComplete();
	}

	@Test
	void productosConMayorStockPorSucursal_sucursalesNull_devuelveVacio() {
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(null).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.productosConMayorStockPorSucursal("f1"))
				.assertNext(List::isEmpty)
				.verifyComplete();
	}

	@Test
	void actualizarNombreFranquicia_actualiza() {
		Franquicia f = Franquicia.builder().id("f1").nombre("Old").sucursales(new ArrayList<>()).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.actualizarNombreFranquicia("f1", " New "))
				.assertNext(actual -> assertThat(actual.getNombre()).isEqualTo("New"))
				.verifyComplete();
	}

	@Test
	void actualizarNombreFranquicia_nombreInvalido_lanzaBadRequest() {
		BadRequestException ex = assertThrows(BadRequestException.class, () -> service.actualizarNombreFranquicia("f1", " "));
		assertThat(ex.getMessage()).contains("obligatorio");
	}

	@Test
	void actualizarNombreSucursal_actualiza() {
		Sucursal s = Sucursal.builder().id("s1").nombre("Old").productos(new ArrayList<>()).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.actualizarNombreSucursal("f1", "s1", " Centro "))
				.assertNext(actual -> assertThat(actual.getSucursales().get(0).getNombre()).isEqualTo("Centro"))
				.verifyComplete();
	}

	@Test
	void actualizarNombreProducto_actualiza() {
		Producto p = Producto.builder().id("p1").nombre("Old").stock(1).build();
		Sucursal s = Sucursal.builder().id("s1").nombre("S").productos(new ArrayList<>(List.of(p))).build();
		Franquicia f = Franquicia.builder().id("f1").nombre("F").sucursales(new ArrayList<>(List.of(s))).build();
		when(repository.findById("f1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.actualizarNombreProducto("f1", "s1", "p1", " Nuevo "))
				.assertNext(actual -> assertThat(actual.getSucursales().get(0).getProductos().get(0).getNombre()).isEqualTo("Nuevo"))
				.verifyComplete();
	}
}

package com.ecommerce.service;

import com.ecommerce.ejercicio.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CarritoServiceTest {

    CarritoService carrito;
    Producto laptop;
    Producto mouse;

    @BeforeEach
    void setUp() {
        carrito = new CarritoService();
        laptop  = new Producto("Laptop", 4500.0, 10);
        mouse   = new Producto("Mouse",   150.0,  50);
    }

    // ── 1. assertEquals ─────────────────────────────────────────
    @Test
    @DisplayName("calcularTotal: laptop + mouse → 4650.0")
    void calcularTotal_dosProductos_sumaPrecios() {
        carrito.agregarProducto(laptop);
        carrito.agregarProducto(mouse);

        double total = carrito.calcularTotal();

        assertEquals(4650.0, total);
    }

    // ── 2. assertEquals (caso borde) ────────────────────────────
    @Test
    @DisplayName("calcularTotal: carrito vacío → 0.0")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0.0, carrito.calcularTotal());
    }

    // ── 3. assertThrows ─────────────────────────────────────────
    @Test
    @DisplayName("agregarProducto: stock 0 → lanza IllegalStateException")
    void agregarProducto_sinStock_lanzaExcepcion() {
        Producto agotado = new Producto("Teclado", 200.0, 0);

        assertThrows(IllegalStateException.class,
                () -> carrito.agregarProducto(agotado));
    }

    // ── 4. assertThrows + getMessage ────────────────────────────
    @Test
    @DisplayName("agregarProducto: null → mensaje de error correcto")
    void agregarProducto_null_mensajeDeError() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carrito.agregarProducto(null));

        assertTrue(ex.getMessage().contains("null"));
    }

    // ── 5. assertTrue / assertFalse ─────────────────────────────
    @Test
    @DisplayName("vaciar: después de vaciar el carrito está vacío")
    void vaciar_carritoConProductos_quedaVacio() {
        carrito.agregarProducto(laptop);
        assertFalse(carrito.estaVacio()); // antes: tiene items

        carrito.vaciar();

        assertTrue(carrito.estaVacio());  // después: vacío
        assertEquals(0, carrito.getItems().size());
    }

    // ── 6. assertNotNull / assertNull ───────────────────────────
    @Test
    @DisplayName("buscarProducto: encuentra existente, null si no existe")
    void buscarProducto_existenteYNoExistente() {
        carrito.agregarProducto(laptop);

        assertNotNull(carrito.buscarProducto("Laptop"));
        assertNull(carrito.buscarProducto("Teclado"));
    }

    // ── 7. assertAll ────────────────────────────────────────────
    @Test
    @DisplayName("obtenerResumen: verifica cantidad, total y vacio juntos")
    void obtenerResumen_dosProductos_camposCorrrectos() {
        carrito.agregarProducto(laptop);
        carrito.agregarProducto(mouse);

        Map<String, Object> resumen = carrito.obtenerResumen();

        assertAll("resumen del carrito",
                () -> assertEquals(2,      resumen.get("cantidad")),
                () -> assertEquals(4650.0, resumen.get("total")),
                () -> assertEquals(false,   resumen.get("vacio"))
        );
    }

    // ── 8. assertThrows (otro tipo de excepción) ────────────────
    @Test
    @DisplayName("obtenerMasCaro: carrito vacío → NoSuchElementException")
    void obtenerMasCaro_carritoVacio_lanzaExcepcion() {
        assertThrows(NoSuchElementException.class,
                () -> carrito.obtenerMasCaro());
    }

    // ── 9. @ParameterizedTest + assertThrows ────────────────────
    @ParameterizedTest
    @ValueSource(doubles = {0, -5, 101, 200})
    @DisplayName("aplicarDescuento: porcentaje inválido → excepción")
    void aplicarDescuento_porcentajeInvalido_lanzaExcepcion(double pct) {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarDescuento(pct));
    }

    // ── 10. assertNotEquals + assertEquals ──────────────────────
    @Test
    @DisplayName("aplicarDescuento: 10% → total distinto al original")
    void aplicarDescuento_10pct_totalDistintoAlOriginal() {
        carrito.agregarProducto(laptop); // 4500.0

        double conDescuento = carrito.aplicarDescuento(10);

        assertNotEquals(4500.0, conDescuento);   // distinto al original
        assertEquals(4050.0,  conDescuento);   // 4500 - 10% = 4050
    }
}
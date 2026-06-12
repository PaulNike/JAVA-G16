package com.ecommerce.service;

import com.ecommerce.ejercicio.Producto;

import java.util.*;

/**
 * Servicio de carrito de compras — Java puro, sin Spring.
 *
 * Métodos diseñados para practicar distintos tipos de assertions:
 *   agregarProducto()  → assertEquals, assertThrows, assertTrue
 *   calcularTotal()    → assertEquals, assertNotEquals
 *   aplicarDescuento() → assertEquals, assertThrows
 *   obtenerResumen()   → assertNotNull, assertTrue, assertAll
 *   vaciar()           → assertTrue (lista vacía)
 *   estaVacio()        → assertTrue, assertFalse
 *   buscarProducto()   → assertNotNull, assertNull
 *   obtenerMasCaro()   → assertThrows (lista vacía)
 */
public class CarritoService {

    private final List<Producto> items = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        if (producto == null)
            throw new IllegalArgumentException("El producto no puede ser null");
        if (producto.getStock() == 0)
            throw new IllegalStateException(
                    "Sin stock: " + producto.getNombre());
        items.add(producto);
    }

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();
    }

    public double aplicarDescuento(double porcentaje) {
        if (porcentaje <= 0 || porcentaje > 100)
            throw new IllegalArgumentException(
                    "Porcentaje inválido: " + porcentaje);
        double total = calcularTotal();
        return total - (total * porcentaje / 100);
    }

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("cantidad", items.size());
        resumen.put("total",    calcularTotal());
        resumen.put("vacio",    items.isEmpty());
        return resumen;
    }

    public void vaciar() {
        items.clear();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }


    public Producto buscarProducto(String nombre) {
        return items.stream()
                .filter(p -> p.getNombre()
                        .equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public Producto obtenerMasCaro() {
        return items.stream()
                .max(Comparator.comparingDouble(Producto::getPrecio))
                .orElseThrow(() -> new NoSuchElementException(
                        "El carrito está vacío"));
    }

    public List<Producto> getItems() { return Collections.unmodifiableList(items); }
}
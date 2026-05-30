package com.codigo.api_exception.repository;

import com.codigo.api_exception.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductoRepository {

    private final AtomicLong contadorId = new AtomicLong(1);


    private final List<Producto> productos = new ArrayList<>();

    public ProductoRepository() {

        productos.add(new Producto(contadorId.getAndIncrement(), "Laptop Dell", 2500.00, 10));
        productos.add(new Producto(contadorId.getAndIncrement(), "Mouse Logitech", 80.00, 50));
        productos.add(new Producto(contadorId.getAndIncrement(), "Teclado Mecánico", 150.00, 25));
    }

    public List<Producto> findAll() {
        return new ArrayList<>(productos);
    }

    public Optional<Producto> findById(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Producto save(Producto producto) {
        producto.setId(contadorId.getAndIncrement());
        productos.add(producto);
        return producto;
    }

    public Optional<Producto> update(Long id, Producto datosNuevos) {
        return findById(id).map(productoExistente -> {
            productoExistente.setNombre(datosNuevos.getNombre());
            productoExistente.setPrecio(datosNuevos.getPrecio());
            productoExistente.setStock(datosNuevos.getStock());
            return productoExistente;
        });
    }

    public boolean deleteById(Long id) {
        return productos.removeIf(p -> p.getId().equals(id));
    }
}

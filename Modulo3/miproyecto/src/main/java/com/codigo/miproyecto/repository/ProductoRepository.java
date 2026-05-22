package com.codigo.miproyecto.repository;

import com.codigo.miproyecto.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ProductoRepository {

    private final List<Producto> productos = new ArrayList<Producto>();

    private Long contadoId = 4L;

    public ProductoRepository() {
        productos.add(new Producto(1L,"LAPTOP",2500.00, 5));
        productos.add(new Producto(2L,"MOUSE",100.00, 10));
        productos.add(new Producto(3L,"TECLADO",150.00, 5));
    }

    public List<Producto> listar() {
        return productos;
    }

    public Producto buscarPorId(Long id) {
        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                return producto;
            }
        }
        return null;
    }

    public Producto guardar(Producto producto) {

        producto.setId(contadoId);
        contadoId++;

        productos.add(producto);
        return producto;
    }
}

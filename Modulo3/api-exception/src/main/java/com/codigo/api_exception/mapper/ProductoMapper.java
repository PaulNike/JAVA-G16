package com.codigo.api_exception.mapper;

import com.codigo.api_exception.dto.ProductoRequest;
import com.codigo.api_exception.dto.ProductoResponse;
import com.codigo.api_exception.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse toProductoResponse(Producto producto) {
        /*ProductoResponse productoResponse = new ProductoResponse();
        productoResponse.setId(producto.getId());
        productoResponse.setNombre(producto.getNombre());
        productoResponse.setPrecio(producto.getPrecio());
        productoResponse.setStock(producto.getStock());

        return productoResponse;*/

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock());
    }

    public Producto toProducto(ProductoRequest productoRequest) {
        Producto producto = new Producto();
        producto.setNombre(productoRequest.getNombre());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setStock(productoRequest.getStock());
        return producto;
    }
}

package com.codigo.miproyecto.mapper;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.model.Producto;

public class ProductoMapper {

    public static Producto toProducto(ProductoRequestDTO productoRequestDTO) {

        Producto producto = new Producto();
        producto.setNombre(productoRequestDTO.getNombre());
        producto.setPrecio(productoRequestDTO.getPrecio());
        producto.setStock(productoRequestDTO.getStock());
        return producto;
    }
    public static ProductoResponseDTO toProductoResponseDto(Producto producto, String mensaje) {

        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO();
        productoResponseDTO.setId(producto.getId());
        productoResponseDTO.setNombre(producto.getNombre());
        productoResponseDTO.setPrecio(producto.getPrecio());
        productoResponseDTO.setStock(producto.getStock());
        productoResponseDTO.setMensaje(mensaje);
        return productoResponseDTO;
    }
}

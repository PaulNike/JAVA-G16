package com.codigo.miproyecto.service;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.model.Producto;
import com.codigo.miproyecto.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoServiceImpl {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponseDTO> listarProductos() {

        List<Producto> productos = productoRepository.listar();
        List<ProductoResponseDTO> productosDTO = new ArrayList<>();

        for (Producto producto : productos) {
            ProductoResponseDTO productoDTO = new ProductoResponseDTO();
            productoDTO.setId(producto.getId());
            productoDTO.setNombre(producto.getNombre());
            productoDTO.setPrecio(producto.getPrecio());
            productoDTO.setStock(producto.getStock());
            productoDTO.setMensaje("Producto Listado con exito");

            productosDTO.add(productoDTO);
        }

        return productosDTO;
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.buscarPorId(id);
    }

    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {

        Producto producto = new Producto();
        producto.setNombre(requestDTO.getNombre());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());

        Producto productoGuardado = productoRepository.guardar(producto);

        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setId(productoGuardado.getId());
        responseDTO.setNombre(productoGuardado.getNombre());
        responseDTO.setPrecio(productoGuardado.getPrecio());
        responseDTO.setStock(productoGuardado.getStock());
        responseDTO.setMensaje("Producto creado correctamente");

        return responseDTO;

    }


}

package com.codigo.miproyecto.controller;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.model.Producto;
import com.codigo.miproyecto.repository.ProductoRepository;
import com.codigo.miproyecto.service.ProductoServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoServiceImpl productoService;

    public ProductoController(ProductoServiceImpl productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponseDTO> listarProductos() {
        return productoService.listarProductos();
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public Producto obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    @GetMapping(value = "/busqueda", produces = MediaType.APPLICATION_XML_VALUE)
    public Producto obtenerProductoPorIdParam(@RequestParam Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    @PostMapping
    public ProductoResponseDTO createProducto(@RequestBody ProductoRequestDTO producto) {
        return productoService.crearProducto(producto);
    }

}

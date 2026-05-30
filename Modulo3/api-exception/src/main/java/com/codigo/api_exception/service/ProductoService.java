package com.codigo.api_exception.service;

import com.codigo.api_exception.dto.ProductoRequest;
import com.codigo.api_exception.dto.ProductoResponse;
import com.codigo.api_exception.exceptions.RecursoNoEncontradoException;
import com.codigo.api_exception.mapper.ProductoMapper;
import com.codigo.api_exception.model.Producto;
import com.codigo.api_exception.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProductoResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductoResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse buscarPorId(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto con ID " + id + " no encontrado"
                ));
        return mapper.toProductoResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto nuevo = mapper.toProducto(request);
        Producto guardado = repository.save(nuevo);
        return mapper.toProductoResponse(guardado);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto con ID " + id + " no encontrado"
                ));

        Producto datosNuevos = mapper.toProducto(request);
        Producto actualizado = repository.update(id, datosNuevos)
                .orElseThrow();

        return mapper.toProductoResponse(actualizado);
    }

    public void eliminar(Long id) {
        boolean eliminado = repository.deleteById(id);
        if (!eliminado) {
            throw new RecursoNoEncontradoException(
                    "Producto con ID " + id + " no encontrado"
            );
        }
    }
}

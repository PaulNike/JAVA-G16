package com.codigo.service;

import com.codigo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Product create(Product product);
    Product update(Long id, Product datosNuevos);
    void delete(Long id);
    Page<Product> findAllPaginado(int page, int size, String paramSort, String direccion);
    List<Product> findAllNoPaginado();
}

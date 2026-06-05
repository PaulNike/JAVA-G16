package com.codigo.service.impl;

import com.codigo.entity.Product;
import com.codigo.repository.ProductRepository;
import com.codigo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Product create(Product product) {
        log.info("Creando producto: {}", product.getName());

        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + product.getName());
        }

        Product saved = productRepository.save(product);
        log.info("Producto creado con id: {}", saved.getId());
        return saved;
    }

    @Transactional
    @Override
    public Product update(Long id, Product datosNuevos) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        product.setName(datosNuevos.getName());
        product.setPrice(datosNuevos.getPrice());
        product.setStock(datosNuevos.getStock());

        //productRepository.save(product);
        //vovler a guardar
        log.info("Producto {} actualizado (dirty checking activo)", id);
        return product;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Producto {} eliminado", id);
    }

    @Override
    public Page<Product> findAllPaginado(int page, int size, String paramSort, String direccion) {
        Sort.Direction direction = "desc".equalsIgnoreCase(direccion) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, paramSort));
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAllNoPaginado() {
        return productRepository.findAll();
    }
}

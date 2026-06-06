package com.ecommerce.service;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Buscando todos los productos");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        // orElseThrow → si no existe, lanza EntityNotFoundException
        // Nuestro GlobalExceptionHandler la convierte en respuesta HTTP 404
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getByCategory(Category category) {
        log.info("Buscando productos de categoría: {}", category);
        return productRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getByMaxPrice(Double maxPrice) {
        return productRepository.findByPriceLessThan(maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Product> search(String query) {
        log.info("Buscando productos con nombre que contenga: '{}'", query);
        return productRepository.searchByName(query);
    }


    @Transactional(readOnly = true)
    public Page<Product> getByCategoryPaginated(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("price").ascending()
        );
        return productRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return productRepository.findAll(pageable);
    }


    @Transactional
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
    public Product update(Long id, Product datosNuevos) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        product.setName(datosNuevos.getName());
        product.setPrice(datosNuevos.getPrice());
        product.setStock(datosNuevos.getStock());
        product.setCategory(datosNuevos.getCategory());

        log.info("Producto {} actualizado (dirty checking activo)", id);
        return product;
    }


    @Transactional
    public void decrementStock(Long productId, Integer quantity) {
        int updated = productRepository.decrementStock(productId, quantity);
        if (updated == 0) {
            throw new IllegalStateException(
                    "No se pudo decrementar el stock del producto " + productId +
                    ". Posiblemente stock insuficiente.");
        }
        log.info("Stock decrementado: producto={}, cantidad={}", productId, quantity);
    }


    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Producto {} eliminado", id);
    }

    @Transactional(readOnly = true)
    public long countByCategory(Category category) {
        return productRepository.countByCategory(category);
    }
}

package com.ecommerce.controller;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products  o  /api/products?page=0&size=10&sort=price
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sort) {

        if (page != null) {
            // Con paginación: devuelve Page<Product>
            Page<Product> result = productService.getAllPaginated(page, size, sort);
            return ResponseEntity.ok(Map.of(
                    "content", result.getContent(),
                    "totalElements", result.getTotalElements(),
                    "totalPages", result.getTotalPages(),
                    "currentPage", result.getNumber(),
                    "hasNext", result.hasNext()
            ));
        }
        // Sin paginación: devuelve la lista completa
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // GET /api/products/category/ELECTRONICS
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    // GET /api/products/search?q=laptop
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String q) {
        return ResponseEntity.ok(productService.search(q));
    }

    // GET /api/products/max-price?price=200
    @GetMapping("/max-price")
    public ResponseEntity<List<Product>> getByMaxPrice(@RequestParam Double price) {
        return ResponseEntity.ok(productService.getByMaxPrice(price));
    }

    // POST /api/products
    // Body: {"name":"Auriculares","price":120.0,"stock":25,"category":"ELECTRONICS"}
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    // PATCH /api/products/{id}/stock
    // Body: {"quantity": 2}
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Map<String, String>> decrementStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        productService.decrementStock(id, body.get("quantity"));
        return ResponseEntity.ok(Map.of("message", "Stock actualizado correctamente"));
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    // GET /api/products/stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalProducts", productService.getAllProducts().size(),
                "electronics", productService.countByCategory(Category.ELECTRONICS),
                "clothing", productService.countByCategory(Category.CLOTHING),
                "sports", productService.countByCategory(Category.SPORTS),
                "food", productService.countByCategory(Category.FOOD)
        ));
    }
}

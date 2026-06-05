package com.codigo.controller;

import com.codigo.entity.Product;
import com.codigo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @GetMapping("/paginado")
    public ResponseEntity<?> busquedaConPaginacion(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direccion) {
        if(page != null) {
            //COn Paginacion
            Page<Product> resutlado = productService.findAllPaginado(page, size, sort, direccion);
            return ResponseEntity.ok(Map.of(
                    "content",resutlado.getContent(),
                    "TotalElements",resutlado.getTotalElements(),
                    "totalPages", resutlado.getTotalPages(),
                    "currentPages", resutlado.getNumber(),
                    "hasNext", resutlado.hasNext()
            ));
        }
        //Ejecuto la consulta sin paginacion
        return ResponseEntity.ok(productService.findAllNoPaginado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

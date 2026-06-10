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

/**
 * SERVICE: ProductService
 * ========================
 * Capa de negocio. Aquí va la lógica de la aplicación.
 *
 * PRINCIPIOS DEMOSTRADOS EN ESTA CLASE:
 *
 * 1. @Transactional en el Service (no en el Repository):
 *    - Mejor control del alcance de la transacción
 *    - Permite combinar múltiples operaciones en una sola transacción
 *    - @Transactional(readOnly=true) en consultas: optimización de rendimiento
 *
 * 2. Ciclo de vida de entidades:
 *    - Dentro de @Transactional: las entidades están en estado MANAGED
 *    - Dirty checking: si modificas una entidad MANAGED, Hibernate genera
 *      el UPDATE automáticamente sin necesidad de repo.save()
 *
 * 3. Paginación con Pageable:
 *    - PageRequest.of(page, size, sort) construye el objeto Pageable
 *    - Page<T> devuelve los datos + metadatos (total, páginas, etc.)
 *
 * @Slf4j → Lombok genera: private static final Logger log = LoggerFactory.getLogger(ProductService.class);
 * @RequiredArgsConstructor → Lombok genera constructor con todos los campos final (inyección por constructor)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    // Inyección por constructor (mejor práctica vs @Autowired en campo)
    private final ProductRepository productRepository;

    // ----------------------------------------------------------------
    // OPERACIONES DE LECTURA — @Transactional(readOnly = true)
    // ----------------------------------------------------------------

    /**
     * readOnly = true le dice a Hibernate:
     *   - No hagas dirty checking (no rastrees cambios)
     *   - Optimiza el flush mode
     *   - Permite que la BD use réplicas de solo lectura
     * Úsalo siempre en métodos que solo leen datos.
     */
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

    /**
     * PAGINACIÓN CON Pageable
     * =======================
     * PageRequest.of(page, size, sort):
     *   page → número de página (0-indexed: primera página = 0)
     *   size → cuántos elementos por página
     *   sort → ordenamiento (campo + dirección)
     *
     * Page<Product> incluye:
     *   .getContent()       → List<Product> de esta página
     *   .getTotalElements() → total de registros en BD
     *   .getTotalPages()    → total de páginas
     *   .getNumber()        → página actual
     *   .hasNext()          → existe página siguiente?
     *   .hasPrevious()      → existe página anterior?
     */
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

    // ----------------------------------------------------------------
    // OPERACIONES DE ESCRITURA — @Transactional
    // ----------------------------------------------------------------

    /**
     * CREAR PRODUCTO
     * ==============
     * repo.save(product) cuando product.getId() == null → genera INSERT
     * repo.save(product) cuando product.getId() != null → genera UPDATE (merge)
     *
     * Ciclo de vida:
     *   new Product(...) → estado: NEW
     *   repo.save(p)     → estado: MANAGED → INSERT → id asignado
     *   return p         → p sale del método con su id asignado por la BD
     */
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

    /**
     * ACTUALIZAR CON DIRTY CHECKING
     * ==============================
     * Dentro de @Transactional, las entidades recuperadas están en estado MANAGED.
     * Si modificas algún campo, Hibernate genera automáticamente el UPDATE
     * al terminar la transacción (en el flush/commit).
     * NO necesitas llamar repo.save() al final.
     *
     * Este es el "dirty checking" de Hibernate.
     */
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

    /**
     * ACTUALIZAR STOCK — Usando @Modifying + @Query
     * ==============================================
     * Cuando necesitas actualizar muchos registros de una vez,
     * es más eficiente usar una query UPDATE directa que:
     *   1. Cargar todas las entidades (N SELECTs)
     *   2. Modificar cada una
     *   3. Hacer N UPDATEs
     *
     * decrementStock genera un solo UPDATE atómico.
     */
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

    /**
     * ELIMINAR
     * ========
     * deleteById lanza EmptyResultDataAccessException si el id no existe.
     * Lo manejamos con existsById primero para dar un mensaje más claro.
     */
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Producto {} eliminado", id);
    }

    // ----------------------------------------------------------------
    // ESTADÍSTICAS
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public long countByCategory(Category category) {
        return productRepository.countByCategory(category);
    }
}

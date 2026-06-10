package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * REPOSITORY: ProductRepository
 * ==============================
 * Extiende JpaRepository<Product, Long>:
 *   - Product → tipo de la entidad
 *   - Long    → tipo del ID (debe coincidir con el @Id de la entidad)
 *
 * Métodos GRATIS que hereda de JpaRepository:
 *   save(entity)          → INSERT o UPDATE (según si tiene id o no)
 *   saveAll(list)         → INSERT/UPDATE en batch
 *   findById(id)          → SELECT WHERE id=? → devuelve Optional<Product>
 *   findAll()             → SELECT * FROM products
 *   findAll(Pageable)     → SELECT con LIMIT/OFFSET (paginación)
 *   findAll(Sort)         → SELECT con ORDER BY
 *   deleteById(id)        → DELETE WHERE id=?
 *   deleteAll()           → DELETE FROM products
 *   existsById(id)        → SELECT COUNT(*) > 0
 *   count()               → SELECT COUNT(*)
 *   flush()               → fuerza el sync con la BD antes del commit
 *
 * QUERY METHODS DERIVADOS (convención de nombre):
 * ================================================
 * Spring Data JPA analiza el nombre del método y genera el SQL automáticamente.
 * Estructura: findBy + Campo + Condición + [And|Or] + Campo2 + OrderBy + Campo3 + [Asc|Desc]
 *
 * ADVERTENCIA IMPORTANTE:
 * Si el nombre del método no coincide con un campo real de la entidad,
 * Spring lanza PropertyReferenceException al ARRANCAR la app, no en runtime.
 * Esto es una ventaja: fallas rápido y con un mensaje claro.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ----------------------------------------------------------------
    // EJERCICIO S1 — Query Methods básicos (de la PPT)
    // ----------------------------------------------------------------

    /**
     * findByCategory → SELECT * FROM products WHERE category = ?
     * Hibernate traduce Category.ELECTRONICS → "ELECTRONICS" (por @Enumerated(STRING))
     */
    List<Product> findByCategory(Category category);

    /**
     * findByPriceLessThan → SELECT * FROM products WHERE price < ?
     * Condiciones disponibles: LessThan, GreaterThan, Between, Like, Containing, IsNull...
     */
    List<Product> findByPriceLessThan(Double maxPrice);

    /**
     * BONUS del ejercicio: combina dos campos con And
     * → SELECT * FROM products WHERE stock > ? AND category = ?
     */
    List<Product> findByStockGreaterThanAndCategory(Integer minStock, Category category);

    // ----------------------------------------------------------------
    // QUERY METHODS con ordenamiento
    // ----------------------------------------------------------------

    /**
     * OrderByPriceAsc → añade ORDER BY price ASC al SQL generado
     */
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    /**
     * findTop3By → agrega LIMIT 3 al SQL
     * TOP/FIRST + número limita resultados sin Pageable
     */
    List<Product> findTop3ByCategoryOrderByPriceDesc(Category category);

    // ----------------------------------------------------------------
    // PAGINACIÓN CON Pageable
    // ----------------------------------------------------------------

    /**
     * Cuando se combina un Query Method con Pageable, Spring genera
     * automáticamente los SELECTs paginados Y el COUNT(*) para saber
     * el total de elementos.
     *
     * Uso:
     *   Pageable p = PageRequest.of(0, 10, Sort.by("price").descending());
     *   Page<Product> page = repo.findByCategory(Category.ELECTRONICS, p);
     *   page.getContent()       → lista de productos de esta página
     *   page.getTotalElements() → total en BD (ej. 47)
     *   page.getTotalPages()    → total de páginas (ej. 5)
     *   page.hasNext()          → hay más páginas?
     */
    Page<Product> findByCategory(Category category, Pageable pageable);

    // ----------------------------------------------------------------
    // @QUERY con JPQL (Jakarta Persistence Query Language)
    // ----------------------------------------------------------------

    /**
     * JPQL usa nombres de CLASE JAVA y ATRIBUTOS, NO nombres de tabla SQL.
     *
     * Diferencia:
     *   SQL:   SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%laptop%')
     *   JPQL:  SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%'))
     *                         ↑ clase Java       ↑ atributo Java
     *
     * Hibernate traduce JPQL al dialecto SQL correcto (H2, PostgreSQL, MySQL).
     * Esto hace que tu código sea portátil entre bases de datos.
     *
     * @Param("q") → vincula :q en el JPQL con el parámetro del método
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> searchByName(@Param("q") String query);

    /**
     * Búsqueda por rango de precio con JPQL
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findByPriceBetween(@Param("min") Double min, @Param("max") Double max);

    /**
     * COUNT personalizado con JPQL
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :cat AND p.stock > 0")
    long countAvailableByCategory(@Param("cat") Category category);

    // ----------------------------------------------------------------
    // @Modifying: para UPDATE y DELETE masivos
    // ----------------------------------------------------------------

    /**
     * @Modifying → indica que este @Query modifica datos (no es SELECT)
     * @Transactional → OBLIGATORIO en operaciones @Modifying
     *                  (si no está aquí, debe estar en el Service que lo llame)
     *
     * Retorna int: número de filas afectadas
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") Long productId, @Param("qty") Integer quantity);

    // ----------------------------------------------------------------
    // Métodos de conteo y existencia
    // ----------------------------------------------------------------

    /** countBy → SELECT COUNT(*) WHERE category = ? */
    long countByCategory(Category category);

    /** existsBy → SELECT COUNT(*) > 0 WHERE name = ? */
    boolean existsByName(String name);

    // ----------------------------------------------------------------
    // Subquery: productos que nunca han sido pedidos
    // ----------------------------------------------------------------

    /**
     * Devuelve productos que NO aparecen en ningún OrderItem.
     * Usa subquery NOT IN sobre la tabla order_items.
     * Requerido por el test de integración OrderRelationsTest.
     */
    @Query("SELECT p FROM Product p WHERE p.id NOT IN " +
           "(SELECT DISTINCT i.product.id FROM OrderItem i)")
    List<Product> findProductsNeverOrdered();
}

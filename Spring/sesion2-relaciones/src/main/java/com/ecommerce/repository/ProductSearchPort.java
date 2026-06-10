package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;

import java.util.List;

/**
 * Sesión 1 — Principio de Segregación de Interfaces (I de SOLID)
 *
 * Aquí la analogía clásica de ISP: una impresora multifunción
 * tiene print(), scan(), fax() y copy(). Si solo necesito imprimir
 * y me entregan la interfaz completa, tengo que saber sobre fax()
 * aunque nunca lo llame. Eso es un acoplamiento innecesario.
 *
 * Lo mismo pasa con ProductRepository: tiene save(), delete(),
 * decrementStock(), existsByName(), findByPriceBetween(), y muchos
 * más. Un servicio de catálogo de productos (para mostrar el catálogo
 * al usuario final) solo necesita buscar por nombre y por categoría.
 *
 * Si ese servicio depende de ProductRepository completo:
 *   - Tiene acceso accidental a decrementStock() que no debería tocar
 *   - Sus tests deben mockear toda la interfaz grande
 *   - El código no comunica con claridad qué capacidades usa
 *
 * Con ProductSearchPort: el servicio de catálogo solo ve búsquedas.
 * La intención queda clara en el código. Los tests son más simples.
 * Y nadie puede usar decrementStock() por error.
 *
 * ProductRepository puede implementar esta interfaz además de JpaRepository.
 * Son contratos que coexisten: uno para operaciones completas,
 * otro para búsquedas solamente.
 */
public interface ProductSearchPort {

    /**
     * Búsqueda parcial por nombre (LIKE %q%).
     */
    List<Product> searchByName(String q);

    /**
     * Todos los productos de una categoría específica.
     */
    List<Product> findByCategory(Category category);
}

package com.ecommerce.repository;

import com.ecommerce.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Sesion 2 - CustomerRepository: queries con @EntityGraph y JPQL
 *
 * Noten que aqui @EntityGraph aparece en findAll().
 * Esto significa que TODA consulta de clientes carga sus ordenes en la misma query.
 * En produccion, eso podria ser costoso si un cliente tiene muchos pedidos.
 * Para este proyecto academico es aceptable; en produccion usariamos paginacion.
 *
 * findCustomersWithOrderCount() devuelve List<Object[]>.
 * Cada Object[] tiene: [0] = nombre del cliente (String), [1] = cantidad de ordenes (Long).
 * El Service debe hacer el casting explicito al convertirlo.
 * Esto es una alternativa mas simple a las proyecciones con interfaces.
 *
 * findCustomersWithoutOrders() usa una subquery: NOT IN (SELECT ...).
 * Es la forma JPQL de hacer un LEFT JOIN WHERE IS NULL.
 * Util para identificar clientes que nunca han comprado.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Query Method derivado: SELECT * FROM customers WHERE email = ?
    Optional<Customer> findByEmail(String email);

    // LEFT JOIN FETCH: trae el customer CON su lista de ordenes (aunque este vacia)
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<Customer> findByIdWithOrders(@Param("id") Long id);

    // Reporte: cuantos pedidos ha hecho cada cliente (incluye los que no han comprado)
    @Query("SELECT c.name, COUNT(o) FROM Customer c LEFT JOIN c.orders o GROUP BY c.id, c.name")
    List<Object[]> findCustomersWithOrderCount();

    // Clientes que nunca han hecho un pedido
    @Query("SELECT c FROM Customer c WHERE c.id NOT IN (SELECT DISTINCT o.customer.id FROM Order o)")
    List<Customer> findCustomersWithoutOrders();

    // @EntityGraph en findAll(): carga customers y sus orders en 1 query (no N+1)
    @EntityGraph(attributePaths = {"orders"})
    List<Customer> findAll();
}

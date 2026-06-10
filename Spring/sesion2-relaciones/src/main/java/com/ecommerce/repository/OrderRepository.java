package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Sesion 2 - OrderRepository: el corazon de las queries avanzadas
 *
 * Este repository concentra las dos soluciones al problema N+1:
 *
 *   SOLUCION 1: JOIN FETCH en JPQL
 *     Escribimos el JOIN explicitamente en la query.
 *     Control total sobre que se carga y como.
 *
 *   SOLUCION 2: @EntityGraph
 *     Declaramos que atributos cargar mediante anotacion.
 *     Mas limpio visualmente, Spring Data genera el JOIN por nosotros.
 *     Ambas generan el mismo SQL; la diferencia es en la expresividad del codigo.
 *
 * Los estudiantes van a llamar los endpoints /demo/n1-problem, /demo/join-fetch
 * y /demo/entity-graph para ver en consola la diferencia de queries.
 *
 * Noten el uso de List<Object[]> en las queries de reporte: cuando el resultado
 * no es una entidad completa (ej. nombre + suma), devolvemos arreglos de Object
 * y el Service los convierte al tipo apropiado (String, Double, Long).
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Query Method derivado: Spring genera SELECT * FROM orders WHERE status = ?
    List<Order> findByStatus(OrderStatus status);

    // SOLUCION N+1: JOIN FETCH
    // Un solo SELECT con JOIN trae orders + customers en 1 query, no N+1

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status")
    List<Order> findByStatusWithCustomer(@Param("status") OrderStatus status);

    // JOIN FETCH de 3 niveles: orders + items + products en una sola query
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product " +
           "WHERE o.status = :status")
    List<Order> findByStatusWithFullDetails(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithCustomer(@Param("id") Long id);

    // SOLUCION N+1: @EntityGraph
    // Misma semantica que JOIN FETCH pero declarativa

    @EntityGraph(attributePaths = {"customer"})
    List<Order> findWithCustomerByStatus(OrderStatus status);

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Order> findWithFullDetailsByStatus(OrderStatus status);

    // findWithDetailById es el que usa OrderService.getOrderWithFullDetails()
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Order> findWithDetailById(Long id);

    // REPORTES CON JPQL AVANZADO

    // GROUP BY + SUM: ventas totales por cliente, ordenadas de mayor a menor
    @Query("SELECT c.name, SUM(o.total) " +
           "FROM Order o JOIN o.customer c " +
           "GROUP BY c.id, c.name " +
           "ORDER BY SUM(o.total) DESC")
    List<Object[]> getSalesByCustomer();

    // Filtro multi-condicion: pedidos caros de un cliente especifico
    @Query("SELECT o FROM Order o JOIN FETCH o.customer " +
           "WHERE o.customer.id = :customerId AND o.total > :minTotal")
    List<Order> findExpensiveOrdersByCustomer(
            @Param("customerId") Long customerId,
            @Param("minTotal") Double minTotal);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer " +
           "WHERE o.customer.id = :customerId " +
           "ORDER BY o.createdAt DESC")
    Optional<Order> findLatestOrderByCustomer(@Param("customerId") Long customerId);

    // UPDATE MASIVO CON @Modifying
    // Un solo UPDATE para todos los pedidos viejos, sin cargar entidades en memoria

    @Modifying
    @Query("UPDATE Order o SET o.status = 'CANCELLED' " +
           "WHERE o.status = 'PENDING' AND o.createdAt < :cutoffDate")
    int cancelOldPendingOrders(@Param("cutoffDate") LocalDateTime cutoffDate);

    // GROUP BY + COUNT: cuantos pedidos hay en cada estado
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    // Subquery: pedidos que contienen un producto especifico
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.customer " +
           "JOIN o.items i " +
           "WHERE i.product.id = :productId")
    List<Order> findOrdersContainingProduct(@Param("productId") Long productId);
}

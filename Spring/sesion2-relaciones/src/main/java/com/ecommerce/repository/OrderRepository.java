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


public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByStatus(OrderStatus status);


    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status")
    List<Order> findByStatusWithCustomer(@Param("status") OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.customer " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product " +
           "WHERE o.status = :status")
    List<Order> findByStatusWithFullDetails(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithCustomer(@Param("id") Long id);


    @EntityGraph(attributePaths = {"customer"})
    List<Order> findWithCustomerByStatus(OrderStatus status);


    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Order> findWithFullDetailsByStatus(OrderStatus status);


    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Order> findWithDetailById(Long id);


    @Query("SELECT c.name, SUM(o.total) " +
           "FROM Order o JOIN o.customer c " +
           "GROUP BY c.id, c.name " +
           "ORDER BY SUM(o.total) DESC")
    List<Object[]> getSalesByCustomer();

    @Query("SELECT o FROM Order o JOIN FETCH o.customer " +
           "WHERE o.customer.id = :customerId AND o.total > :minTotal")
    List<Order> findExpensiveOrdersByCustomer(
            @Param("customerId") Long customerId,
            @Param("minTotal") Double minTotal);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer " +
           "WHERE o.customer.id = :customerId " +
           "ORDER BY o.createdAt DESC")
    Optional<Order> findLatestOrderByCustomer(@Param("customerId") Long customerId);


    @Modifying
    @Query("UPDATE Order o SET o.status = 'CANCELLED' " +
           "WHERE o.status = 'PENDING' AND o.createdAt < :cutoffDate")
    int cancelOldPendingOrders(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.customer " +
           "JOIN o.items i " +
           "WHERE i.product.id = :productId")
    List<Order> findOrdersContainingProduct(@Param("productId") Long productId);
}

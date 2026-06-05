package com.ecommerce.repository;

import com.ecommerce.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);


    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<Customer> findByIdWithOrders(@Param("id") Long id);


    @Query("SELECT c.name, COUNT(o) FROM Customer c LEFT JOIN c.orders o GROUP BY c.id, c.name")
    List<Object[]> findCustomersWithOrderCount();


    @Query("SELECT c FROM Customer c WHERE c.id NOT IN (SELECT DISTINCT o.customer.id FROM Order o)")
    List<Customer> findCustomersWithoutOrders();


    @EntityGraph(attributePaths = {"orders"})
    List<Customer> findAll();
}

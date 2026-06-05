package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);


    @Query("SELECT p FROM Product p WHERE p.id NOT IN " +
           "(SELECT DISTINCT i.product.id FROM OrderItem i)")
    List<Product> findProductsNeverOrdered();


    @Query("SELECT p.name, SUM(i.quantity) as totalSold " +
           "FROM OrderItem i JOIN i.product p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> searchByName(@Param("q") String query);

    boolean existsByName(String name);
}

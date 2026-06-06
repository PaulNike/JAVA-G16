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


public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByCategory(Category category);


    List<Product> findByPriceLessThan(Double maxPrice);


    List<Product> findByStockGreaterThanAndCategory(Integer minStock, Category category);


    List<Product> findByCategoryOrderByPriceAsc(Category category);


    List<Product> findTop3ByCategoryOrderByPriceDesc(Category category);


    Page<Product> findByCategory(Category category, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> searchByName(@Param("q") String query);


    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findByPriceBetween(@Param("min") Double min, @Param("max") Double max);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :cat AND p.stock > 0")
    long countAvailableByCategory(@Param("cat") Category category);


    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") Long productId, @Param("qty") Integer quantity);


    long countByCategory(Category category);


    boolean existsByName(String name);
}

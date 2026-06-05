package com.codigo.repository;

import com.codigo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    List<Customer> findByNameContaining(String name);
    //@Procedure(procedureName = "nombreDeSuProcedimiento")
    //List<Customer> listarClientesPorNombre(@Param("texto") String texto);

    //QUERY NATIVO
    @Query(value = """
            SELECT *
            FROM customers 
            WHERE name LIKE CONCAT('%', :name, '%')
            """, nativeQuery = true)
    List<Customer> buscarPorNombreQueryNativo(@Param("name") String name);

    //JPQL
    @Query(value = """
            SELECT c
            FROM Customer c
            WHERE c.name LIKE CONCAT('%', :name, '%')
            """)
    List<Customer> buscarPorNombreJpql(@Param("name") String name);


    @Query(value = """
             SELECT *
                FROM customers
                WHERE id IN (
                        SELECT id
                                    FROM customers
                                            WHERE pg_sleep(5) IS NULL)
            """, nativeQuery = true)
    List<Customer> consultaLente(@Param("name") String name);

}


package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  Punto de entrada del proyecto E-Commerce
 *
 * Esta aplicacion usa una base de datos en memoria H2.
 * No necesitan crear nada en PostgreSQL: arranca directo con:
 *
 *   mvn spring-boot:run
 *
 * Los datos iniciales vienen de src/main/resources/data.sql.
 * La consola H2 esta disponible en http://localhost:8080/h2-console
 *   JDBC URL: jdbc:h2:mem:ecommerce
 *   User: sa  |  Password: (vacio)
 *
 * DEMOSTRACION EN CLASE: el problema N+1 y sus soluciones.
 * Con la app corriendo, llamen estos endpoints en orden y observen
 * la consola: cuantas queries SQL ejecuta Hibernate en cada caso.
 *
 *   1. GET /api/orders/demo/n1-problem   -> 1 + N queries (el problema)
 *   2. GET /api/orders/demo/join-fetch   -> 1 sola query  (solucion JPQL)
 *   3. GET /api/orders/demo/entity-graph -> 1 sola query  (solucion declarativa)
 *
 * Para crear un pedido de prueba:
 *   POST /api/orders
 *   Body: {"customerId":1,"productIds":[1,2],"quantities":[2,1]}
 */
@SpringBootApplication
public class Sesion2Application {

    public static void main(String[] args) {
        SpringApplication.run(Sesion2Application.class, args);
    }
}

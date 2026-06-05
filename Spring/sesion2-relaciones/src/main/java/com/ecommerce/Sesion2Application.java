package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SESION 2 - Relaciones, Fetching y el Problema N+1
 * ==================================================
 * Antes de arrancar, crea la BD en PostgreSQL:
 *   CREATE DATABASE ecommerce_s2;
 *
 * Luego arranca con:
 *   mvn spring-boot:run
 *
 * Para ver el N+1 en accion:
 *   1. Con la app corriendo, llama: GET /api/orders/demo/n1-problem
 *   2. Observa la consola: veras 1 + N queries (una por cada pedido)
 *   3. Llama: GET /api/orders/demo/join-fetch
 *   4. Observa la consola: veras 1 sola query con JOIN
 *   5. Llama: GET /api/orders/demo/entity-graph
 *   6. Mismo resultado: 1 sola query
 */
@SpringBootApplication
public class Sesion2Application {

    public static void main(String[] args) {
        SpringApplication.run(Sesion2Application.class, args);
    }
}

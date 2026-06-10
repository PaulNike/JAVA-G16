package com.ecommerce.repository;

import java.util.List;

/**
 * Sesión 1 — Principio de Segregación de Interfaces (I de SOLID)
 *
 * Piensen en este escenario: tenemos un ReportService que solo
 * necesita generar reportes de ventas. Para eso solo requiere
 * dos métodos: getSalesByCustomer() y countByStatus().
 *
 * Si ReportService inyectara OrderRepository directamente, recibiría
 * también deleteById(), cancelOldPendingOrders(), save(), y más de
 * quince métodos que nunca va a usar. ¿Cuál es el riesgo?
 *
 *   1. Un desarrollador nuevo ve deleteById() disponible en ReportService
 *      y lo usa pensando que tiene permiso. Nadie se lo impidió.
 *   2. Los tests de ReportService deben mockear todo OrderRepository,
 *      incluyendo métodos que no se usan. Más código, más complejidad.
 *
 * ISP dice: "ningún cliente debe depender de métodos que no usa."
 * Esta interfaz pequeña es la solución: ReportService depende de
 * OrderQueryPort y solo tiene acceso a lo que realmente necesita.
 *
 * OrderRepository puede implementar esta interfaz además de JpaRepository.
 * Ambas coexisten; solo cambia qué ve cada cliente.
 */
public interface OrderQueryPort {

    /**
     * Total de ventas agrupado por nombre de cliente.
     * Usado por el módulo de reportes de negocio.
     */
    List<Object[]> getSalesByCustomer();

    /**
     * Conteo de pedidos agrupado por estado.
     * Usado por el dashboard de operaciones.
     */
    List<Object[]> countByStatus();
}

package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sesion 2 - OrderController: exposicion REST y endpoints de demostracion
 *
 * Este controller hace dos cosas:
 *
 * 1. CRUD basico de pedidos (POST, GET por id, GET por status).
 *    No hay PUT ni DELETE: en e-commerce, los pedidos raramente se modifican;
 *    se cancelan (cambio de estado).
 *
 * 2. Endpoints de DEMOSTRACION del problema N+1 y sus soluciones.
 *    Durante la clase, van a llamar estos endpoints en orden y observar
 *    en la consola cuantas queries ejecuta Hibernate en cada caso:
 *
 *    GET /api/orders/demo/n1-problem   -> problema: 1 + N queries
 *    GET /api/orders/demo/join-fetch   -> solucion: 1 query con JOIN FETCH
 *    GET /api/orders/demo/entity-graph -> solucion: 1 query con @EntityGraph
 *
 * 3. Endpoints de REPORTE con JPQL avanzado:
 *    GET /api/orders/reports/sales-by-customer -> GROUP BY + SUM en JPQL
 *    GET /api/orders/reports/count-by-status   -> GROUP BY + COUNT en JPQL
 *
 * La arquitectura HTTP aqui es: Controller -> Service -> Repository.
 * El controller no conoce nada de JPA ni de descuentos: solo HTTP.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders
    // Body: {"customerId":1,"productIds":[1,2],"quantities":[2,1]}
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        List<Integer> rawProductIds = (List<Integer>) body.get("productIds");
        List<Integer> quantities    = (List<Integer>) body.get("quantities");
        List<Long> productIds = rawProductIds.stream().map(Long::valueOf).toList();
        return orderService.createOrder(customerId, productIds, quantities);
    }

    // GET /api/orders
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderWithFullDetails(id));
    }

    // GET /api/orders/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getByStatus(status));
    }

    // DEMOSTRACION N+1 - llamen estos tres endpoints en orden y comparen queries

    // GET /api/orders/demo/n1-problem -> observen: 1 SELECT orders + N SELECT customer
    @GetMapping("/demo/n1-problem")
    public ResponseEntity<List<String>> demoProblemaN1() {
        return ResponseEntity.ok(orderService.demoN1Problem());
    }

    // GET /api/orders/demo/join-fetch -> 1 sola query con JOIN
    @GetMapping("/demo/join-fetch")
    public ResponseEntity<List<String>> demoJoinFetch() {
        return ResponseEntity.ok(orderService.solveN1WithJoinFetch(OrderStatus.PENDING));
    }

    // GET /api/orders/demo/entity-graph -> mismo resultado que join-fetch, diferente declaracion
    @GetMapping("/demo/entity-graph")
    public ResponseEntity<List<String>> demoEntityGraph() {
        return ResponseEntity.ok(orderService.solveN1WithEntityGraph(OrderStatus.PENDING));
    }

    // REPORTES

    // GET /api/orders/reports/sales-by-customer
    @GetMapping("/reports/sales-by-customer")
    public ResponseEntity<Map<String, Double>> salesByCustomer() {
        return ResponseEntity.ok(orderService.getSalesByCustomer());
    }

    // GET /api/orders/reports/count-by-status
    @GetMapping("/reports/count-by-status")
    public ResponseEntity<Map<String, Long>> countByStatus() {
        return ResponseEntity.ok(orderService.getOrderCountByStatus());
    }

    // GET /api/orders/customer/{customerId}/expensive?minTotal=500
    @GetMapping("/customer/{customerId}/expensive")
    public ResponseEntity<List<Order>> expensiveOrders(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "100") Double minTotal) {
        return ResponseEntity.ok(orderService.getExpensiveOrdersByCustomer(customerId, minTotal));
    }

    // POST /api/orders/cancel-old?daysOld=30
    @PostMapping("/cancel-old")
    public ResponseEntity<Map<String, Object>> cancelOldOrders(
            @RequestParam(defaultValue = "30") int daysOld) {
        int count = orderService.cancelOldOrders(daysOld);
        return ResponseEntity.ok(Map.of(
                "message", "Pedidos cancelados",
                "count", count,
                "daysOld", daysOld
        ));
    }
}

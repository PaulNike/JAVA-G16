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


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        List<Integer> rawProductIds = (List<Integer>) body.get("productIds");
        List<Integer> quantities    = (List<Integer>) body.get("quantities");
        List<Long> productIds = rawProductIds.stream().map(Long::valueOf).toList();
        return orderService.createOrder(customerId, productIds, quantities);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderWithFullDetails(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getByStatus(status));
    }


    @GetMapping("/demo/n1-problem")
    public ResponseEntity<List<String>> demoProblemaN1() {
        return ResponseEntity.ok(orderService.demoN1Problem());
    }

    @GetMapping("/demo/join-fetch")
    public ResponseEntity<List<String>> demoJoinFetch() {
        return ResponseEntity.ok(orderService.solveN1WithJoinFetch(OrderStatus.PENDING));
    }

    @GetMapping("/demo/entity-graph")
    public ResponseEntity<List<String>> demoEntityGraph() {
        return ResponseEntity.ok(orderService.solveN1WithEntityGraph(OrderStatus.PENDING));
    }


    @GetMapping("/reports/sales-by-customer")
    public ResponseEntity<Map<String, Double>> salesByCustomer() {
        return ResponseEntity.ok(orderService.getSalesByCustomer());
    }

    @GetMapping("/reports/count-by-status")
    public ResponseEntity<Map<String, Long>> countByStatus() {
        return ResponseEntity.ok(orderService.getOrderCountByStatus());
    }

    @GetMapping("/customer/{customerId}/expensive")
    public ResponseEntity<List<Order>> expensiveOrders(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "100") Double minTotal) {
        return ResponseEntity.ok(orderService.getExpensiveOrdersByCustomer(customerId, minTotal));
    }


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

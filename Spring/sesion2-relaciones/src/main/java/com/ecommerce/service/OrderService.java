package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;


    @Transactional
    public Order createOrder(Long customerId, List<Long> productIds, List<Integer> quantities) {
        log.info("Creando pedido para cliente {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + customerId));

        Order order = new Order(customer);

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer qty = quantities.get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productId));

            if (product.getStock() < qty) {
                throw new IllegalStateException(
                    "Stock insuficiente para producto '" + product.getName() +
                    "'. Disponible: " + product.getStock() + ", solicitado: " + qty);
            }

            OrderItem item = new OrderItem(product, qty);
            order.addItem(item);

            product.setStock(product.getStock() - qty);

        }

        Order saved = orderRepository.save(order);
        log.info("Pedido creado: id={}, total={}, items={}", saved.getId(), saved.getTotal(), saved.getItems().size());
        return saved;
    }


    @Transactional(readOnly = true)
    public List<String> demoN1Problem() {
        log.warn("=== DEMO N+1: Observa las queries en la consola ===");

        List<Order> orders = orderRepository.findAll();  // 1 query SELECT orders


        return orders.stream()
                .map(o -> "Pedido #" + o.getId() + " → Cliente: " + o.getCustomer().getName()) // N queries aqui
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<String> solveN1WithJoinFetch(OrderStatus status) {
        log.info("=== SOLUCION JOIN FETCH: 1 sola query ===");

        List<Order> orders = orderRepository.findByStatusWithCustomer(status);

        return orders.stream()
                .map(o -> "Pedido #" + o.getId() + " → Cliente: " + o.getCustomer().getName())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<String> solveN1WithEntityGraph(OrderStatus status) {
        log.info("=== SOLUCION @EntityGraph: 1 sola query ===");

        List<Order> orders = orderRepository.findWithCustomerByStatus(status);

        return orders.stream()
                .map(o -> "Pedido #" + o.getId() + " → Cliente: " + o.getCustomer().getName())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Map<String, Double> getSalesByCustomer() {
        return orderRepository.getSalesByCustomer()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Double) row[1]
                ));
    }


    @Transactional(readOnly = true)
    public Order getOrderWithFullDetails(Long id) {
        return orderRepository.findWithDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado: " + id));
    }


    @Transactional
    public int cancelOldOrders(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        int cancelled = orderRepository.cancelOldPendingOrders(cutoff);
        log.info("Cancelados {} pedidos pendientes con mas de {} dias", cancelled, daysOld);
        return cancelled;
    }


    @Transactional(readOnly = true)
    public List<Order> getExpensiveOrdersByCustomer(Long customerId, Double minTotal) {
        return orderRepository.findExpensiveOrdersByCustomer(customerId, minTotal);
    }

    /**
     * Dashboard: conteo de pedidos agrupados por estado.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderCountByStatus() {
        return orderRepository.countByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((OrderStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    /**
     * Todos los pedidos de un estado (con customer cargado).
     */
    @Transactional(readOnly = true)
    public List<Order> getByStatus(OrderStatus status) {
        return orderRepository.findByStatusWithCustomer(status);
    }

    /**
     * Todos los pedidos (con customer cargado via JOIN FETCH).
     */
    @Transactional(readOnly = true)
    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}

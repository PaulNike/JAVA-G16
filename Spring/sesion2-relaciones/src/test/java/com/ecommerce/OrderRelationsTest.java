package com.ecommerce;

import com.ecommerce.entity.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TESTS DE SESION 2 — Relaciones, Fetching y JPQL
 * =================================================
 * Usamos TestEntityManager para tener control total del estado JPA en los tests.
 * TestEntityManager.flush() + clear() es clave para verificar que las queries
 * realmente van a la BD y no usan el cache de primer nivel.
 */
@DataJpaTest
@DisplayName("Sesion 2 — Relaciones, N+1 y JPQL")
class OrderRelationsTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer ana;
    private Customer carlos;
    private Product laptop;
    private Product mouse;
    private Product zapatillas;

    @BeforeEach
    void setUp() {
        ana     = em.persist(new Customer("Ana Garcia",    "ana@test.com",    "Lima"));
        carlos  = em.persist(new Customer("Carlos Perez",  "carlos@test.com", "Arequipa"));
        laptop  = em.persist(new Product("Laptop Dell",  4500.0, 10, Category.ELECTRONICS));
        mouse   = em.persist(new Product("Mouse MX3",     150.0, 50, Category.ELECTRONICS));
        zapatillas = em.persist(new Product("Zapatillas", 199.0, 30, Category.SPORTS));
        em.flush();
    }

    // ----------------------------------------------------------------
    // TESTS DE RELACIONES
    // ----------------------------------------------------------------

    @Test
    @DisplayName("@ManyToOne: FK customer_id se crea en tabla orders")
    void manyToOne_debeCrearFKEnOrders() {
        Order order = new Order(ana);
        order.addItem(new OrderItem(laptop, 1));
        Order saved = em.persistFlushFind(order);

        assertThat(saved.getCustomer()).isNotNull();
        assertThat(saved.getCustomer().getId()).isEqualTo(ana.getId());
        assertThat(saved.getCustomer().getName()).isEqualTo("Ana Garcia");
    }

    @Test
    @DisplayName("CASCADE PERSIST: guardar Order propaga a sus OrderItems")
    void cascade_persist_debeGuardarItems() {
        Order order = new Order(ana);
        order.addItem(new OrderItem(laptop, 1));
        order.addItem(new OrderItem(mouse,  2));

        // Solo hacemos save del Order, NO de los items individualmente
        Order saved = orderRepository.save(order);
        em.flush();
        em.clear(); // limpia el cache para forzar una query real

        Order fromDb = orderRepository.findWithDetailById(saved.getId()).orElseThrow();
        assertThat(fromDb.getItems()).hasSize(2);
        assertThat(fromDb.getItems())
                .extracting(i -> i.getProduct().getName())
                .containsExactlyInAnyOrder("Laptop Dell", "Mouse MX3");
    }

    @Test
    @DisplayName("orphanRemoval: quitar item de la lista lo borra de la BD")
    void orphanRemoval_debeEliminarItemHuerfano() {
        // Crear pedido con 2 items
        Order order = new Order(ana);
        OrderItem itemLaptop = new OrderItem(laptop, 1);
        OrderItem itemMouse  = new OrderItem(mouse,  1);
        order.addItem(itemLaptop);
        order.addItem(itemMouse);
        Order saved = orderRepository.save(order);
        em.flush();
        em.clear();

        // Recargar y quitar un item
        Order loaded = orderRepository.findWithDetailById(saved.getId()).orElseThrow();
        loaded.removeItem(loaded.getItems().get(0));
        orderRepository.save(loaded);
        em.flush();
        em.clear();

        // Verificar que solo queda 1 item en la BD
        Order final_ = orderRepository.findWithDetailById(saved.getId()).orElseThrow();
        assertThat(final_.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("total se recalcula al agregar items")
    void total_debeRecalcularseAlAgregarItems() {
        Order order = new Order(ana);
        order.addItem(new OrderItem(laptop, 1)); // 4500
        order.addItem(new OrderItem(mouse,  2)); // 150 x 2 = 300
        order.recalculateTotal();

        assertThat(order.getTotal()).isEqualTo(4800.0); // 4500 + 300
    }

    // ----------------------------------------------------------------
    // TESTS DE JOIN FETCH (solucion N+1)
    // ----------------------------------------------------------------

    @Test
    @DisplayName("findByStatusWithCustomer: carga Customer en 1 query (no N+1)")
    void joinFetch_debeCargaCustomerEnUnaQuery() {
        Order o1 = orderRepository.save(new Order(ana));
        Order o2 = orderRepository.save(new Order(carlos));
        em.flush();
        em.clear(); // limpiar cache para forzar queries reales

        // Esta query usa JOIN FETCH → Customer ya esta cargado
        List<Order> orders = orderRepository.findByStatusWithCustomer(OrderStatus.PENDING);

        assertThat(orders).hasSize(2);
        // Acceder a customer NO genera queries adicionales porque ya fue cargado con JOIN FETCH
        orders.forEach(o -> {
            assertThat(o.getCustomer()).isNotNull();
            assertThat(o.getCustomer().getName()).isNotBlank();
        });
    }

    @Test
    @DisplayName("@EntityGraph: equivalente a JOIN FETCH de forma declarativa")
    void entityGraph_debeCargaCustomerSinN1() {
        orderRepository.save(new Order(ana));
        orderRepository.save(new Order(carlos));
        em.flush();
        em.clear();

        List<Order> orders = orderRepository.findWithCustomerByStatus(OrderStatus.PENDING);

        assertThat(orders).hasSize(2);
        orders.forEach(o -> assertThat(o.getCustomer().getName()).isNotBlank());
    }

    @Test
    @DisplayName("findWithDetailById: carga Customer + Items + Products en 1 query")
    void entityGraph_debeCargaDetallesCompletos() {
        Order order = new Order(ana);
        order.addItem(new OrderItem(laptop, 1));
        order.addItem(new OrderItem(mouse,  2));
        Order saved = orderRepository.save(order);
        em.flush();
        em.clear();

        Order loaded = orderRepository.findWithDetailById(saved.getId()).orElseThrow();

        assertThat(loaded.getCustomer().getName()).isEqualTo("Ana Garcia");
        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getItems().get(0).getProduct()).isNotNull();
    }

    // ----------------------------------------------------------------
    // TESTS DE JPQL AVANZADO
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getSalesByCustomer: GROUP BY + SUM retorna ventas correctas")
    void jpql_groupBy_debeSumarVentasPorCliente() {
        Order o1 = new Order(ana);
        o1.addItem(new OrderItem(laptop, 1)); // 4500
        o1.setTotal(4500.0);
        orderRepository.save(o1);

        Order o2 = new Order(ana);
        o2.addItem(new OrderItem(mouse, 2)); // 300
        o2.setTotal(300.0);
        orderRepository.save(o2);

        Order o3 = new Order(carlos);
        o3.addItem(new OrderItem(zapatillas, 1)); // 199
        o3.setTotal(199.0);
        orderRepository.save(o3);
        em.flush();
        em.clear();

        List<Object[]> result = orderRepository.getSalesByCustomer();

        assertThat(result).hasSize(2);
        // Ana primero (mayor total: 4800)
        assertThat((String) result.get(0)[0]).isEqualTo("Ana Garcia");
        assertThat((Double) result.get(0)[1]).isEqualTo(4800.0);
    }

    @Test
    @DisplayName("cancelOldPendingOrders: @Modifying actualiza N filas en 1 query")
    void modifying_debeCancelarPedidosAntiguos() {
        Order recent = new Order(ana);
        recent.setStatus(OrderStatus.PENDING);
        recent.setCreatedAt(java.time.LocalDateTime.now().minusDays(5));
        orderRepository.save(recent);

        Order old = new Order(carlos);
        old.setStatus(OrderStatus.PENDING);
        old.setCreatedAt(java.time.LocalDateTime.now().minusDays(40));
        orderRepository.save(old);
        em.flush();
        em.clear();

        int cancelled = orderRepository.cancelOldPendingOrders(
                java.time.LocalDateTime.now().minusDays(30));

        assertThat(cancelled).isEqualTo(1); // solo el pedido antiguo
        assertThat(orderRepository.findById(old.getId()).get().getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderRepository.findById(recent.getId()).get().getStatus())
                .isEqualTo(OrderStatus.PENDING); // el reciente no cambia
    }

    @Test
    @DisplayName("findProductsNeverOrdered: subquery detecta productos sin ventas")
    void subquery_debeDetectarProductosSinPedidos() {
        // Solo laptop y mouse se piden
        Order order = new Order(ana);
        order.addItem(new OrderItem(laptop, 1));
        order.addItem(new OrderItem(mouse, 1));
        orderRepository.save(order);
        em.flush();
        em.clear();

        // zapatillas no se pidio
        List<Product> neverOrdered = productRepository.findProductsNeverOrdered();

        assertThat(neverOrdered).hasSize(1);
        assertThat(neverOrdered.get(0).getName()).isEqualTo("Zapatillas");
    }
}

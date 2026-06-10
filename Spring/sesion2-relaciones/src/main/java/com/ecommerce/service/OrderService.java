package com.ecommerce.service;

import com.ecommerce.discount.DiscountStrategy;
import com.ecommerce.entity.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sesión 1 — Refactor SOLID de OrderService
 *
 * Antes de ver el código, lean la versión original en el historial de Git
 * y compárenla con esta. La pregunta que quiero que se hagan es:
 * ¿qué cambió y por qué cada cambio importa para los tests de Sesión 2?
 *
 * Lo que hacía createOrder() antes (todo mezclado):
 *   - Buscaba el cliente
 *   - Buscaba cada producto
 *   - Validaba el stock con un if inline
 *   - Creaba el OrderItem
 *   - Decrementaba el stock
 *   - Guardaba el pedido
 *   - ... y no notificaba ni aplicaba descuentos
 *
 * Lo que hace ahora (cada responsabilidad en su lugar):
 *   - Coordina: busca datos, delega validación, delega construcción,
 *     delega cálculo, aplica descuento, persiste, notifica
 *
 * Esto es SRP: OrderService ya no es el experto en todo.
 * Es un coordinador que sabe a quién delegar cada tarea.
 *
 * ─────────────────────────────────────────────────────────────────
 * SOBRE LA INYECCIÓN DE DEPENDENCIAS (D de SOLID)
 * ─────────────────────────────────────────────────────────────────
 * Observen cómo inyectamos NotificationService como INTERFAZ, no como
 * LogNotificationService. Spring decide en tiempo de ejecución qué
 * implementación usar. En tests, Mockito puede reemplazarla.
 *
 * Lo mismo aplica a List<DiscountStrategy>: Spring detecta todas las
 * clases anotadas con @Component que implementen esa interfaz y las
 * inyecta automáticamente en esta lista. Para agregar un nuevo descuento
 * solo creamos la clase; OrderService no cambia.
 *
 * ─────────────────────────────────────────────────────────────────
 * POR QUÉ ESTO IMPORTA PARA LA SESIÓN 2 (MOCKITO)
 * ─────────────────────────────────────────────────────────────────
 * Cada colaborador inyectado como interfaz puede reemplazarse con un
 * Mock en los tests unitarios:
 *
 *   @Mock StockValidator stockValidator
 *   → controlamos si lanza excepción o no sin código real
 *
 *   @Mock NotificationService notifier
 *   → verificamos que se llamó sin enviar ningún log o email real
 *
 *   @Mock OrderFactory orderFactory
 *   → controlamos exactamente qué Order devuelve sin construir nada
 *
 * Sin este diseño, testear createOrder() requeriría una base de datos
 * real y todo el contexto de Spring. Con este diseño, el test corre
 * en milisegundos con @ExtendWith(MockitoExtension.class).
 */
@Service
@Slf4j
public class OrderService {

    // ── Repositories ──────────────────────────────────────────────
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    // ── Colaboradores especializados (SRP: uno por responsabilidad) ─
    private final NotificationService notificationService;
    private final StockValidator stockValidator;
    private final PriceCalculator priceCalculator;
    private final OrderFactory orderFactory;

    // List<DiscountStrategy> se inyecta con @Autowired en campo separado
    // porque Mockito @InjectMocks usa el constructor, y una List<>
    // de interfaces no puede ser inyectada automáticamente por constructor.
    // Con este diseño, los tests funcionan sin configuración adicional.
    @Autowired
    private List<DiscountStrategy> discountStrategies = new ArrayList<>();

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        NotificationService notificationService,
                        StockValidator stockValidator,
                        PriceCalculator priceCalculator,
                        OrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.stockValidator = stockValidator;
        this.priceCalculator = priceCalculator;
        this.orderFactory = orderFactory;
    }


    // ─────────────────────────────────────────────────────────────────
    // MÉTODO PRINCIPAL — lean este método junto con la explicación
    // de arriba para entender cada decisión de diseño
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    public Order createOrder(Long customerId, List<Long> productIds, List<Integer> quantities) {
        log.info("Creando pedido para cliente {}", customerId);

        // Paso 1: buscar el cliente
        // Si no existe, EntityNotFoundException se propaga hasta GlobalExceptionHandler
        // que la convierte en HTTP 404. Observen ese manejo en el controller.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + customerId));

        // Paso 2: por cada producto, validar stock y decrementar
        // La validación la delega en StockValidator (SRP).
        // El decremento aprovecha el dirty checking de Hibernate:
        // como el producto es un managed entity dentro de @Transactional,
        // Hibernate detecta el cambio en stock y genera el UPDATE automáticamente
        // al hacer flush, sin que llamemos explícitamente a save(product).
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer qty = quantities.get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productId));

            stockValidator.validate(product, qty); // lanza IllegalStateException si no hay stock
            product.setStock(product.getStock() - qty); // dirty checking — Hibernate lo persiste
            products.add(product);
        }

        // Paso 3: construir el Order con todos sus items (Factory Pattern)
        Order order = orderFactory.create(customer, products, quantities);

        // Paso 4: calcular el total bruto explícitamente (SRP)
        // OrderFactory ya llama a recalculateTotal() internamente.
        // Usamos PriceCalculator aquí para obtener ese número antes
        // de aplicar el descuento, y para que el flujo sea legible.
        Double rawTotal = priceCalculator.calculate(order.getItems());

        // Paso 5: aplicar el descuento correspondiente (Strategy Pattern + OCP)
        Double finalTotal = applyDiscount(customer, rawTotal);
        order.setTotal(finalTotal);

        // Paso 6: persistir
        Order saved = orderRepository.save(order);

        // Paso 7: notificar (DIP — a través de la interfaz, sin conocer la implementación)
        notificationService.notifyOrderCreated(saved);

        log.info("Pedido creado: id={}, total={}, items={}", saved.getId(), saved.getTotal(), saved.getItems().size());
        return saved;
    }

    /**
     * Aplica la primera estrategia de descuento aplicable para el cliente.
     * Si la lista está vacía o ninguna aplica, devuelve el total sin cambio.
     *
     * Con NoDiscount en la lista, el orElse(total) nunca debería ejecutarse.
     * Lo dejamos como salvaguarda defensiva.
     */
    private Double applyDiscount(Customer customer, Double total) {
        return discountStrategies.stream()
                .filter(strategy -> strategy.isApplicable(customer))
                .findFirst()
                .map(strategy -> strategy.apply(total))
                .orElse(total);
    }


    // ─────────────────────────────────────────────────────────────────
    // MÉTODOS DE DEMOSTRACIÓN Y REPORTES — sin cambios respecto al
    // proyecto original. Estos muestran las soluciones al problema N+1
    // y las queries avanzadas de JPQL que vimos en sesiones anteriores.
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<String> demoN1Problem() {
        log.warn("=== DEMO N+1: Observa las queries en la consola ===");
        // findAll() trae todas las órdenes en 1 query
        List<Order> orders = orderRepository.findAll();
        // Aquí Hibernate ejecuta N queries más para cargar cada customer.lazy
        return orders.stream()
                .map(o -> "Pedido #" + o.getId() + " → Cliente: " + o.getCustomer().getName())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> solveN1WithJoinFetch(OrderStatus status) {
        log.info("=== SOLUCION JOIN FETCH: 1 sola query ===");
        // JOIN FETCH en el JPQL carga orders Y customers en una sola query
        List<Order> orders = orderRepository.findByStatusWithCustomer(status);
        return orders.stream()
                .map(o -> "Pedido #" + o.getId() + " → Cliente: " + o.getCustomer().getName())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> solveN1WithEntityGraph(OrderStatus status) {
        log.info("=== SOLUCION @EntityGraph: 1 sola query ===");
        // @EntityGraph es la alternativa declarativa a JOIN FETCH
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

    @Transactional(readOnly = true)
    public Map<String, Long> getOrderCountByStatus() {
        return orderRepository.countByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((OrderStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    @Transactional(readOnly = true)
    public List<Order> getByStatus(OrderStatus status) {
        return orderRepository.findByStatusWithCustomer(status);
    }

    @Transactional(readOnly = true)
    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}

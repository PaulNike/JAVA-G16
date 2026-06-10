package com.ecommerce.unit;

import com.ecommerce.discount.VipDiscount;
import com.ecommerce.entity.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Sesión 2 — Tests con Mockito: OrderService
 *
 * Este test clase es el corazón de la Sesión 2. Aquí conectamos todo
 * lo que vimos: el refactor SOLID de Sesión 1 habilita exactamente
 * lo que hacemos aquí.
 *
 * ¿Qué es un Mock?
 * Un Mock es un "doble de prueba": un objeto que se comporta como el
 * real pero que nosotros controlamos. Podemos decirle qué retornar,
 * verificar que fue llamado, o hacer que lance una excepción.
 *
 * ¿Por qué necesitamos Mocks aquí?
 * OrderService depende de 7 colaboradores. En un test unitario NO
 * queremos que esos colaboradores ejecuten código real (no queremos
 * base de datos, no queremos emails, no queremos lógica de negocio
 * de otros módulos). Queremos aislar OrderService y probar SOLO su
 * coordinación interna.
 *
 * Los Mocks que usamos y para qué:
 *
 *   @Mock CustomerRepository   → controlamos si el cliente "existe" o no
 *   @Mock ProductRepository    → controlamos si el producto "existe" o no
 *   @Mock OrderRepository      → verificamos que save() fue llamado
 *   @Mock NotificationService  → verificamos que se notificó sin log real
 *   @Mock StockValidator       → controlamos si lanza excepción o no
 *   @Mock PriceCalculator      → controlamos el total que devuelve
 *   @Mock OrderFactory         → controlamos el Order que construye
 *
 * @InjectMocks crea una instancia de OrderService pasando todos los
 * @Mock al constructor. Las 7 dependencias quedan bajo nuestro control.
 *
 * discountStrategies NO se inyecta por constructor (es un campo @Autowired),
 * así que @InjectMocks lo deja como lista vacía. Para el test de descuento
 * usamos ReflectionTestUtils para inyectarlo manualmente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService — unit tests con Mockito")
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    NotificationService notificationService;

    @Mock
    StockValidator stockValidator;

    @Mock
    PriceCalculator priceCalculator;

    @Mock
    OrderFactory orderFactory;

    @InjectMocks
    OrderService orderService;

    // Datos de prueba reutilizables entre tests
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        // Creamos entidades reales (no mocks) para los datos de dominio.
        // Los repositories son mocks, pero los objetos que devuelven son reales.
        customer = new Customer("Ana Test", "ana@test.com", null);
        product = new Product("Laptop Test", 500.0, 10, Category.ELECTRONICS);
    }


    // ─────────────────────────────────────────────────────────────
    // TEST 1 — Happy path: el flujo completo funciona
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createOrder exitoso: guarda el pedido y notifica al cliente")
    void createOrder_exitoso_guardaYNotifica() {
        // ARRANGE — configurar todos los mocks para el camino feliz
        Order order = new Order(customer);
        order.addItem(new OrderItem(product, 2));

        // when(...).thenReturn(...) — "cuando se llame esto, devuelve aquello"
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // stockValidator.validate() es void → no lanza por defecto (comportamiento del mock)
        when(orderFactory.create(any(), anyList(), anyList())).thenReturn(order);
        when(priceCalculator.calculate(anyList())).thenReturn(1000.0);
        when(orderRepository.save(any())).thenReturn(order);

        // ACT
        Order resultado = orderService.createOrder(1L, List.of(1L), List.of(2));

        // ASSERT
        assertNotNull(resultado, "El resultado no debe ser null");

        // verify(...) — "verificar que este método fue llamado exactamente N veces"
        // Si save() no se llamó, el test falla aunque el resultado no sea null.
        verify(orderRepository, times(1)).save(any(Order.class));

        // Verificamos también que se notificó: esto prueba el DIP en acción.
        // NotificationService es un mock — no se ejecutó ningún log real.
        verify(notificationService, times(1)).notifyOrderCreated(any(Order.class));
    }


    // ─────────────────────────────────────────────────────────────
    // TEST 2 — Stock insuficiente: el error se propaga correctamente
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createOrder con stock insuficiente: lanza excepción, NUNCA guarda")
    void createOrder_stockInsuficiente_lanzaExcepcion() {
        // ARRANGE
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // doThrow().when() — la forma de hacer que un método VOID lance excepción.
        // Noten que no usamos when().thenThrow() porque validate() es void.
        doThrow(new IllegalStateException("Stock insuficiente para producto 'Laptop Test'"))
                .when(stockValidator).validate(any(Product.class), any(Integer.class));

        // ACT & ASSERT — assertThrows verifica que la excepción se lanza
        assertThrows(
                IllegalStateException.class,
                () -> orderService.createOrder(1L, List.of(1L), List.of(5)),
                "Debe propagarse la excepción cuando StockValidator falla"
        );

        // ASSERT adicional — never() verifica que NUNCA se llamó
        // Si el sistema guardara el pedido a pesar del error de stock,
        // tendríamos inconsistencia en la base de datos.
        verify(orderRepository, never()).save(any());
        verify(notificationService, never()).notifyOrderCreated(any());
    }


    // ─────────────────────────────────────────────────────────────
    // TEST 3 — Cliente no encontrado: falla antes de buscar productos
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createOrder con cliente inexistente: lanza EntityNotFoundException")
    void createOrder_clienteNoExiste_lanzaExcepcion() {
        // ARRANGE — Optional.empty() simula que el cliente no existe en BD
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.createOrder(99L, List.of(1L), List.of(1)),
                "Un cliente inexistente debe lanzar EntityNotFoundException"
        );

        // ASSERT adicional — si el cliente no existe, no debería ni buscar productos
        // Esto verifica que el flujo se corta en el primer error, sin trabajo innecesario.
        verify(productRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }


    // ─────────────────────────────────────────────────────────────
    // TEST 4 — Descuento VIP: Strategy Pattern en acción
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createOrder con cliente VIP: aplica 10% de descuento")
    void createOrder_conDescuento_aplicaStrategyCorrecta() {
        // ARRANGE — activamos el flag VIP en el cliente
        customer.setVip(true);
        Order order = new Order(customer);

        // Inyectamos VipDiscount manualmente en el campo discountStrategies.
        // ReflectionTestUtils es de spring-test: permite acceder a campos privados
        // en tests. Lo usamos porque @Autowired no procesa @InjectMocks.
        ReflectionTestUtils.setField(orderService, "discountStrategies", List.of(new VipDiscount()));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderFactory.create(any(), anyList(), anyList())).thenReturn(order);
        when(priceCalculator.calculate(anyList())).thenReturn(100.0); // total bruto
        // thenAnswer con getArgument(0) devuelve el mismo objeto que se pasa a save()
        // Así podemos inspeccionar el Order después de que fue modificado (setTotal)
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        Order resultado = orderService.createOrder(1L, List.of(1L), List.of(1));

        // ASSERT — VipDiscount aplica 10%: 100.0 × 0.90 = 90.0
        assertEquals(90.0, resultado.getTotal(), 0.001,
                "Cliente VIP con total bruto 100.0 debe pagar 90.0 (10% off)");
    }


    // ─────────────────────────────────────────────────────────────
    // TEST 5 — ArgumentCaptor: inspeccionamos exactamente qué se guardó
    // ─────────────────────────────────────────────────────────────

    /**
     * ArgumentCaptor: herramienta de Mockito para capturar el argumento
     * exacto que se pasó a un método mockeado.
     *
     * ¿Cuándo lo usamos?
     * Cuando verify(mock).metodo(any()) no es suficiente porque queremos
     * inspeccionar el CONTENIDO del objeto que se pasó, no solo saber
     * que se llamó el método.
     *
     * En este test: queremos saber si el Order que se guardó en BD tiene
     * el customer correcto, el total correcto y la cantidad de items correcta.
     * Con any() no podemos verificar eso. Con ArgumentCaptor, sí.
     */
    @Test
    @DisplayName("ArgumentCaptor: verifica el contenido exacto del Order guardado")
    void createOrder_argumentCaptor_verificaOrderGuardado() {
        // ARRANGE
        Order order = new Order(customer);
        OrderItem item = new OrderItem(product, 3); // 3 unidades de Laptop Test
        order.addItem(item);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderFactory.create(any(), anyList(), anyList())).thenReturn(order);
        when(priceCalculator.calculate(anyList())).thenReturn(1500.0); // 500 × 3
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        orderService.createOrder(1L, List.of(1L), List.of(3));

        // ASSERT — creamos el captor del tipo que se pasa a save()
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        // verify con capture() — verifica que se llamó Y guarda lo que se pasó
        verify(orderRepository).save(captor.capture());

        // Ahora inspeccionamos el Order capturado
        Order guardado = captor.getValue();

        assertEquals(customer, guardado.getCustomer(),
                "El Order guardado debe pertenecer al cliente correcto");
        assertEquals(1500.0, guardado.getTotal(), 0.001,
                "El total debe ser 1500.0 (sin descuento — lista de estrategias vacía)");
        assertEquals(1, guardado.getItems().size(),
                "El Order debe contener exactamente 1 item");
    }
}

package com.ecommerce.solid;

/**
 * Patrones de diseño en Spring Boot — material de referencia
 *
 * IMPORTANTE: Este archivo no tiene lógica de producción.
 * Es un catálogo comentado para que identifiquen estos patrones
 * en el código del proyecto y en entrevistas técnicas.
 *
 * Cuando usamos Spring Boot, usamos patrones de diseño todo el tiempo
 * sin necesariamente saber sus nombres. Este archivo los nombra.
 */
@SuppressWarnings("unused")
public class SpringPatternsDemo {

    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN SINGLETON
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: una clase tiene UNA sola instancia en toda la
     * aplicación, y hay un punto de acceso global a ella.
     *
     * En nuestro proyecto:
     *   @Service OrderService    → Spring crea 1 sola instancia
     *   @Service CustomerService → Spring crea 1 sola instancia
     *   @Component StockValidator → Spring crea 1 sola instancia
     *
     * Spring gestiona el ciclo de vida: crea el bean una vez al
     * arrancar y lo reutiliza en cada inyección. No hay synchronized,
     * no hay campo static, no hay getInstance(). El contenedor IoC
     * se encarga de todo.
     *
     * Por eso los @Service deben ser STATELESS (sin estado mutable
     * por request). Si guardaran datos del usuario en un campo de
     * instancia, dos requests concurrentes se interferirían.
     * En nuestros servicios, toda la información viaja como
     * parámetros de método, nunca como estado del objeto.
     */


    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN DEPENDENCY INJECTION (variante de Inversion of Control)
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: las dependencias de un objeto las provee el
     * exterior (el contenedor), no el objeto mismo.
     *
     * En nuestro proyecto (OrderService):
     *   // Correcto — Spring inyecta por constructor:
     *   public OrderService(OrderRepository repo, ...) { ... }
     *
     *   // Incorrecto — el servicio crea su propia dependencia:
     *   private OrderRepository repo = new OrderRepositoryImpl();
     *
     * La inyección por constructor (que usamos) es la forma preferida
     * porque hace explícitas las dependencias (se ven en el constructor),
     * facilita los tests (pasamos mocks directamente) y garantiza que
     * el objeto nunca está en estado inválido.
     *
     * @RequiredArgsConstructor de Lombok genera el constructor
     * automáticamente para todos los campos final.
     */


    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN REPOSITORY
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: abstrae el acceso a la fuente de datos detrás
     * de una interfaz con semántica de colección.
     *
     * En nuestro proyecto:
     *   OrderRepository extends JpaRepository<Order, Long>
     *
     * Los servicios no saben si los datos vienen de PostgreSQL,
     * H2 o un archivo. Por eso los tests de integración (@DataJpaTest)
     * usan H2 en memoria y los tests unitarios usan Mockito.
     * Mismo código de servicio, distintas fuentes de datos.
     *
     * Spring Data genera automáticamente la implementación de
     * JpaRepository en tiempo de ejecución. Nosotros solo
     * declaramos la interfaz y los Query Methods. Es la magia
     * que vimos en la sesión de repositories.
     */


    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN STRATEGY
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: define una familia de algoritmos intercambiables
     * detrás de una interfaz común. El cliente usa la interfaz,
     * no la implementación concreta.
     *
     * En nuestro proyecto (paquete discount):
     *   DiscountStrategy          ← interfaz común
     *   VipDiscount               ← algoritmo: -10%
     *   NewCustomerDiscount       ← algoritmo: -5%
     *   NoDiscount                ← algoritmo: sin cambio
     *
     * OrderService no tiene if/else para decidir el descuento.
     * Itera sobre la lista de estrategias y aplica la primera
     * que sea aplicable. Cuando agregamos BirthdayDiscount,
     * simplemente entra en esa lista. Nadie más cambia.
     */


    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN FACTORY
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: encapsula la construcción de objetos complejos.
     * El cliente pide un objeto sin saber cómo se construye.
     *
     * En nuestro proyecto:
     *   OrderFactory.create(customer, products, quantities) → Order
     *   DiscountStrategyFactory.getFor(customer) → DiscountStrategy
     *
     * Sin OrderFactory, toda la lógica de construcción (iterar
     * productos, crear OrderItems, mantener relaciones bidireccionales)
     * viviría dentro de OrderService. Con el Factory, OrderService
     * delega: "toma estos datos y devuélveme un Order armado".
     *
     * Hay dos variantes de Factory que usamos:
     *   - Factory como @Component inyectado (OrderFactory)
     *   - Factory con método estático utilitario (DiscountStrategyFactory)
     * Elegimos según si el factory necesita dependencias de Spring o no.
     */


    /*
     * ═══════════════════════════════════════════════════════════════
     * PATRÓN NULL OBJECT
     * ═══════════════════════════════════════════════════════════════
     *
     * Definición: en lugar de devolver null cuando no hay valor,
     * se devuelve un objeto que implementa la misma interfaz
     * pero con comportamiento "vacío" o "identidad".
     *
     * En nuestro proyecto:
     *   NoDiscount.apply(total) → devuelve total sin cambio
     *   NoDiscount.isApplicable(customer) → siempre true
     *
     * Esto elimina: if (strategy != null) { ... }
     * Siempre hay una estrategia disponible. El flujo es uniforme.
     *
     * El NullPointerException es el error más frecuente en Java.
     * El Null Object Pattern lo elimina por diseño, no por vigilancia.
     */
}

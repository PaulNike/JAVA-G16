# Proyecto Sesion 2 — Relaciones, Fetching y el Problema N+1

## Que cubre este proyecto

| Tema | Clase / Archivo |
|---|---|
| `@ManyToOne` / `@OneToMany` bidireccional | `Customer.java`, `Order.java`, `OrderItem.java` |
| FK siempre en el lado `@ManyToOne` | `Order.java` (`customer_id`), `OrderItem.java` (`order_id`, `product_id`) |
| `FetchType.LAZY` vs `EAGER` explicado | `Customer.java`, `Order.java`, `OrderItem.java` |
| `CascadeType` correcto (PERSIST/MERGE, nunca ALL en @ManyToOne) | `Customer.java`, `Order.java` |
| `orphanRemoval = true` | `Order.java` |
| Helper bidireccional `addItem()` / `removeItem()` | `Order.java`, `Customer.java` |
| **Problema N+1** (metodo demostrativo) | `OrderService.demoN1Problem()` |
| **Solucion JOIN FETCH** | `OrderRepository.findByStatusWithCustomer()` |
| **Solucion @EntityGraph** | `OrderRepository.findWithCustomerByStatus()` |
| JPQL avanzado: GROUP BY, SUM, subquery, DISTINCT | `OrderRepository.java`, `ProductRepository.java` |
| `@Modifying` UPDATE masivo | `OrderRepository.cancelOldPendingOrders()` |
| Tests con `@DataJpaTest` + `TestEntityManager` | `OrderRelationsTest.java` |

---

## Setup

### 1. Crear la base de datos en PostgreSQL
```sql
CREATE DATABASE ecommerce_s2;
```

### 2. Configurar credenciales
Edita `src/main/resources/application.properties`:
```properties
spring.datasource.username=${DB_USER:tu_usuario}
spring.datasource.password=${DB_PASS:tu_password}
```

### 3. Arrancar
```bash
mvn spring-boot:run
```

### 4. Ejecutar tests (no necesita PostgreSQL)
```bash
mvn test
```

---

## Demo del Problema N+1

Con la app corriendo, abre dos terminales:

**Terminal 1 — ver las queries en tiempo real:**
```bash
# El show-sql=true ya esta activado en application.properties
# Solo observa la consola donde corre mvn spring-boot:run
```

**Terminal 2 — llamar los endpoints:**

```bash
# PASO 1: Causa N+1 → observa MUCHAS queries en la consola
curl http://localhost:8080/api/orders/demo/n1-problem

# PASO 2: Solucion JOIN FETCH → observa 1 sola query con JOIN
curl http://localhost:8080/api/orders/demo/join-fetch

# PASO 3: Solucion @EntityGraph → mismo resultado, 1 sola query
curl http://localhost:8080/api/orders/demo/entity-graph
```

---

## Endpoints disponibles

```bash
# Crear pedido (customerId=1, productos 1 y 2, cantidades 1 y 2)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"productIds":[1,2],"quantities":[1,2]}'

# Todos los pedidos
curl http://localhost:8080/api/orders

# Pedido con detalle completo (Customer + Items + Products)
curl http://localhost:8080/api/orders/1

# Pedidos por estado
curl http://localhost:8080/api/orders/status/PENDING

# Reporte: ventas totales por cliente
curl http://localhost:8080/api/orders/reports/sales-by-customer

# Reporte: conteo por estado
curl http://localhost:8080/api/orders/reports/count-by-status

# Pedidos caros de un cliente (minimo 500)
curl "http://localhost:8080/api/orders/customer/1/expensive?minTotal=500"

# Cancelar pedidos pendientes con mas de 30 dias
curl -X POST "http://localhost:8080/api/orders/cancel-old?daysOld=30"
```

---

## Conceptos clave

### Regla de oro de las FK
```
La FK SIEMPRE va en la tabla del @ManyToOne

customers   (id, name, email)           ← SIN columna FK
orders      (id, status, customer_id)   ← FK aqui (@ManyToOne Customer)
order_items (id, quantity, order_id, product_id) ← FKs aqui
```

### N+1 vs JOIN FETCH
```java
// MAL: N+1
List<Order> orders = repo.findAll();          // 1 query
orders.forEach(o -> o.getCustomer().getName()); // N queries

// BIEN: JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomer(); // 1 sola query con JOIN
```

### CascadeType correcto
```java
// CORRECTO: Order -> OrderItem (hijo depende del padre)
@OneToMany(cascade = {PERSIST, MERGE}, orphanRemoval = true)

// PELIGROSO: nunca ALL en @ManyToOne
@ManyToOne(cascade = ALL) // si borras el Order, borra el Customer!
```

### LAZY vs EAGER
```
@OneToMany  → LAZY por defecto  (correcto, no cargues N hijos sin pedirlo)
@ManyToOne  → EAGER por defecto (cambiarlo a LAZY es buena practica)
@ManyToMany → LAZY por defecto

Regla: siempre LAZY, carga con JOIN FETCH o @EntityGraph cuando necesites.
```

---

## Estructura del proyecto

```
sesion2-relaciones/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/ecommerce/
    │   │   ├── Sesion2Application.java
    │   │   ├── entity/
    │   │   │   ├── Customer.java      ← @OneToMany orders
    │   │   │   ├── Order.java         ← @ManyToOne customer + @OneToMany items
    │   │   │   ├── OrderItem.java     ← @ManyToOne order + @ManyToOne product
    │   │   │   ├── Product.java
    │   │   │   ├── Category.java
    │   │   │   └── OrderStatus.java
    │   │   ├── repository/
    │   │   │   ├── OrderRepository.java    ← JOIN FETCH + @EntityGraph + JPQL
    │   │   │   ├── CustomerRepository.java
    │   │   │   └── ProductRepository.java
    │   │   ├── service/
    │   │   │   └── OrderService.java       ← demo N+1 + soluciones
    │   │   └── controller/
    │   │       ├── OrderController.java
    │   │       └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.properties
    │       └── data.sql
    └── test/
        ├── java/com/ecommerce/
        │   └── OrderRelationsTest.java    ← 8 tests @DataJpaTest
        └── resources/
            └── application.properties    ← H2 para tests
```

---

## Siguiente: Sesion 3

En el **Proyecto Sesion 3** agregaremos:
- Springdoc OpenAPI + Swagger UI
- `@Tag`, `@Operation`, `@ApiResponse` en los controllers
- `@Schema` en los DTOs
- DTOs para no exponer entidades directamente
- `@Valid` + Bean Validation documentado en Swagger
- Manejo de errores estandar RFC 9457 documentado

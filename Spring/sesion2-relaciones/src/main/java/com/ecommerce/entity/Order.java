package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sesion 2 - Entidad Order: el nodo central del grafo de objetos
 *
 * Order tiene DOS relaciones bidireccionales:
 *   1. Order - Customer  (N:1 desde Order, 1:N desde Customer)
 *   2. Order - OrderItem (1:N desde Order, N:1 desde OrderItem)
 *
 * Order es el DUENO de ambas relaciones porque tiene las claves foraneas:
 *   - orders.customer_id referencia a customers
 *   - order_items.order_id referencia a orders
 *
 * El metodo addItem() mantiene sincronizados ambos lados y recalcula el total.
 * Si hacen order.getItems().add(item) sin llamar addItem(),
 * el total quedara desactualizado y item.order no apuntara a este Order.
 * Siempre usen los metodos de conveniencia.
 *
 * Sobre @EnumType.STRING vs ORDINAL:
 *   STRING guarda "PENDING" en la BD (legible, estable).
 *   ORDINAL guarda 0, 1, 2 (compacto pero fragil: reordenar el enum cambia el significado).
 *   En el mundo real siempre usamos STRING.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"customer", "items"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // RELACION @ManyToOne: muchos Orders pertenecen a un Customer
    //
    // Order es el DUENO: tiene la columna customer_id en la tabla orders.
    // fetch = LAZY: el Customer no se carga al traer un Order.
    //   Pero ojo: acceder a order.getCustomer().getName() fuera de una
    //   transaccion activa lanza LazyInitializationException.
    //   Solucion: usar JOIN FETCH en el repository (ver OrderRepository).
    //
    // OPCION 3 (APLICADA): @JsonIgnoreProperties("orders") en este campo.
    //   Al serializar customer dentro de un Order, se omite customer.orders.
    //   Asi el JSON de Order incluye customer (con name, email) pero sin
    //   la lista de ordenes del customer, rompiendo el bucle.
    //
    // OPCION 2: @JsonBackReference("customer-orders") - el customer NO se
    //   serializa dentro de un Order.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("orders")
    private Customer customer;

    // RELACION @OneToMany: un Order tiene muchos OrderItems
    //
    // cascade = {PERSIST, MERGE}: al guardar un Order, sus items se persisten.
    // orphanRemoval = true: si un item se desvincula del Order,
    //   Hibernate lo elimina de la BD automaticamente.
    //
    // OPCION 3 (APLICADA): al serializar un OrderItem dentro de items,
    //   se omite su campo "order". Evitamos el bucle Order - OrderItem - Order.
    @OneToMany(
            mappedBy = "order",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();

    public Order(Customer customer) {
        this.customer = customer;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    // recalculateTotal() existe DENTRO de la entidad porque el total es una
    // derivacion directa de los items. PriceCalculator en el service hace lo mismo
    // pero opera sobre la lista externa antes de que el descuento se aplique.
    public void recalculateTotal() {
        this.total = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
    }
}

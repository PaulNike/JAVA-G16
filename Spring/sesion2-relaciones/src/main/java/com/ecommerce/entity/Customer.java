package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Sesion 2 - Entidad Customer y la relacion bidireccional con Order
 *
 * Esta clase es el punto de partida para entender las relaciones JPA.
 * La pregunta que guia el diseno es: quien es el "dueno" de la relacion?
 *
 * En toda relacion bidireccional hay dos lados:
 *   - El lado DUENO (owner): el que tiene la clave foranea en la BD.
 *     En Customer-Order, Order es el dueno porque tiene customer_id.
 *   - El lado INVERSO (mappedBy): el que referencia al dueno.
 *     Customer es el lado inverso, por eso usamos mappedBy="customer".
 *
 * Pregunta de examen: si agrego un Order directamente a customer.orders
 * sin pasar por el metodo addOrder(), se guarda en la BD?
 * Respuesta: NO. Hibernate persiste desde el lado DUENO (Order).
 * Por eso addOrder() asigna ambos lados: orders.add(order) Y order.setCustomer(this).
 *
 * PROBLEMA DEL BUCLE INFINITO EN JSON:
 * Cuando Jackson serializa un Customer, ve la lista orders.
 * Para serializar cada Order, ve el campo customer.
 * Para serializar ese customer... vuelve a orders. Bucle infinito.
 * Tenemos tres estrategias para romperlo (ver comentarios en el campo @OneToMany).
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "orders")  // exclude evita el mismo bucle en toString()
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 500)
    private String address;

    // Campo requerido por VipDiscount (OCP - paquete discount).
    // Default false: ningun cliente existente se ve afectado al agregar esta columna.
    @Column(nullable = false)
    private boolean vip = false;

    // RELACION @OneToMany: un Customer tiene muchos Orders
    //
    // cascade = {PERSIST, MERGE}: si guardo o fusiono un Customer, la
    //   operacion se propaga a sus Orders. No usamos ALL porque no queremos
    //   que al eliminar un Customer se eliminen sus pedidos.
    //
    // fetch = LAZY: los orders NO se cargan al consultar un Customer.
    //   Se cargan solo cuando llamas customer.getOrders().
    //
    // orphanRemoval = false: si un Order se separa de su Customer,
    //   NO se elimina de la BD. El pedido existe independientemente.
    //
    // ESTRATEGIAS PARA EL BUCLE INFINITO JSON:
    //
    // OPCION 3 (APLICADA) - @JsonIgnoreProperties quirurgico:
    //   Al serializar cada Order dentro de esta lista, se omite su campo "customer".
    //   Rompemos el bucle en el punto exacto donde nace.
    //
    // OPCION 2 - @JsonManagedReference / @JsonBackReference:
    //   Este lado PADRE lleva @JsonManagedReference("customer-orders").
    //   El lado hijo (Order.customer) lleva @JsonBackReference y NO se serializa.
    //
    // OPCION 1 - @JsonIgnore en el lado hijo:
    //   La mas simple: Order.customer lleva @JsonIgnore.
    @OneToMany(
            mappedBy = "customer",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = false
    )
    @JsonIgnoreProperties("customer")
    private List<Order> orders = new ArrayList<>();

    public Customer(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    // Metodo de conveniencia: asigna los dos lados de la relacion.
    // Sin esto, orders.add(order) no actualizaria order.customer en Hibernate.
    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setCustomer(null);
    }
}

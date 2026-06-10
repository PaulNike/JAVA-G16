package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Sesion 2 - Entidad OrderItem: la tabla intermedia del modelo de dominio
 *
 * OrderItem representa un producto dentro de un pedido.
 * Es la tabla intermedia entre Order y Product, pero no es una simple tabla
 * de union (@ManyToMany). Es una entidad completa porque tiene atributos
 * propios: quantity y unitPrice.
 *
 * Por que unitPrice y no usar product.price directamente?
 * Porque el precio puede cambiar manana, pero el precio al que compro
 * el cliente en ese pedido debe quedar grabado para siempre.
 * El constructor captura product.getPrice() en el momento de la compra.
 *
 * Sobre FetchType.EAGER en product:
 * Normalmente preferimos LAZY en todas partes para evitar cargas innecesarias.
 * Aqui usamos EAGER porque siempre necesitamos el producto cuando mostramos
 * un OrderItem (su nombre, precio). EAGER evita el proxy HibernateBytebuddy
 * que causaria problemas de serializacion con Jackson.
 * La alternativa (FetchType.LAZY + @JsonIgnoreProperties en Product) esta
 * comentada en Product.java para que la comparen.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"order", "product"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    // Precio historico: captura product.price en el momento de la compra.
    // No cambia aunque el producto cambie de precio despues.
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    // RELACION con Order: OrderItem es el lado DUENO (tiene order_id)
    //
    // OPCION 3 (APLICADA): @JsonIgnoreProperties("items") omite la lista de
    //   items del Order al serializar. Rompe el bucle Order - OrderItem.
    //
    // OPCION 2: @JsonBackReference("order-items") - el order no se serializa.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("items")
    private Order order;

    // RELACION con Product: carga EAGER para evitar proxy de Hibernate en JSON
    //
    // ALTERNATIVA (FetchType.LAZY):
    //   Si vuelven a LAZY, descomenten en Product.java:
    //   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    //   Eso le dice a Jackson que ignore los campos internos del proxy.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice(); // captura el precio actual
    }

    public Double getSubtotal() {
        return unitPrice * quantity;
    }
}

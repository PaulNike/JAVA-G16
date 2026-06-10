package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Sesion 2 - Entidad Product
 *
 * Product es una entidad "hoja" en nuestro grafo: no tiene relaciones
 * bidireccionales que gestionar. Esto la hace la mas simple de las cuatro.
 *
 * Noten que NO tiene una lista de OrderItems. Por que?
 * Porque no necesitamos navegar desde un producto hacia sus pedidos en el codigo.
 * Si necesitamos esa consulta, la hacemos desde el repository con JPQL.
 * Agregar una coleccion @OneToMany aqui solo agregaria complejidad y riesgo
 * de N+1 sin aportar valor real al dominio de negocio.
 *
 * Sobre el problema del proxy ByteBuddy:
 * Cuando OrderItem.product es FetchType.LAZY, Hibernate entrega un proxy
 * (Product$HibernateProxy) en lugar del Product real. Jackson no sabe como
 * serializar los campos internos del proxy ("hibernateLazyInitializer").
 * Solucion A: OrderItem.product usa FetchType.EAGER (activa en este proyecto).
 * Solucion B: descomentar @JsonIgnoreProperties abajo y cambiar a LAZY en OrderItem.
 */
// Solucion B (alternativa): ignorar campos del proxy de Hibernate
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "orderItems")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public Product(String name, Double price, Integer stock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}

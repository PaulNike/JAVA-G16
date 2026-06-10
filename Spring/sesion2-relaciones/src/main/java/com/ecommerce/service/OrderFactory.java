package com.ecommerce.service;

import com.ecommerce.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sesión 1 — Factory Pattern + Principio de Responsabilidad Única
 *
 * Patrón Factory: cuando construir un objeto requiere varios pasos
 * y decisiones, encapsulamos esa construcción en una clase aparte.
 * El cliente (OrderService) solo pide "dame un Order" y el Factory
 * sabe cómo armarlo correctamente.
 *
 * ¿Qué hace exactamente esta clase?
 * Recibe un Customer y listas de productos con sus cantidades,
 * y devuelve un Order completamente armado con sus OrderItems,
 * manteniendo las relaciones bidireccionales y con el total calculado.
 *
 * Antes, toda esa lógica de construcción vivía en createOrder()
 * de OrderService, mezclada con validaciones, descuentos y
 * persistencia. Era difícil entender dónde terminaba "construir"
 * y dónde empezaba "guardar en base de datos".
 *
 * Ahora el código fluye así:
 *   1. Valido el stock (StockValidator)
 *   2. Construyo la Order (OrderFactory)       ← esta clase
 *   3. Calculo y aplico descuento
 *   4. Guardo (Repository)
 *   5. Notifico (NotificationService)
 *
 * Cada paso es una responsabilidad diferente. Eso es SRP.
 */
@Component
public class OrderFactory {

    /**
     * Construye un Order nuevo con todos sus items.
     *
     * Los productos en la lista ya fueron validados por StockValidator
     * y su stock ya fue decrementado en OrderService antes de llegar aquí.
     * Esta clase no valida nada: solo construye.
     *
     * order.addItem() mantiene la relación bidireccional
     * (item.setOrder(this)) y recalcula el total automáticamente.
     * Observen ese método en la entidad Order.
     */
    public Order create(Customer customer, List<Product> products, List<Integer> quantities) {
        Order order = new Order(customer);
        for (int i = 0; i < products.size(); i++) {
            OrderItem item = new OrderItem(products.get(i), quantities.get(i));
            order.addItem(item);
        }
        return order;
    }
}

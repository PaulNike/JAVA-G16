package com.ecommerce.service;

import com.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sesión 1 — Principio de Responsabilidad Única (S de SOLID)
 *
 * Esta clase calcula el total bruto de un pedido. Solo eso.
 *
 * Noten que Order ya tiene su propio recalculateTotal() que se
 * llama automáticamente al agregar items. Entonces, ¿por qué
 * tener PriceCalculator por separado?
 *
 * Porque la entidad Order pertenece al modelo de datos (JPA),
 * y mezclar lógica de negocio compleja dentro de la entidad
 * la hace difícil de testear y de mantener. PriceCalculator
 * permite calcular totales de forma aislada, pasarle la lista
 * desde donde queramos, y testearlo sin base de datos.
 *
 * Además, en OrderService usamos PriceCalculator para obtener
 * el total ANTES de aplicar el descuento. Ese flujo queda
 * explícito y legible en el código, en lugar de estar oculto
 * en un side-effect de addItem().
 */
@Component
public class PriceCalculator {

    /**
     * Suma el subtotal de cada OrderItem: precio_unitario × cantidad.
     * Si la lista es null o está vacía, devuelve 0.0.
     *
     * Este comportamiento defensivo (tratar null como lista vacía)
     * evita NullPointerExceptions en llamadas legítimas, como cuando
     * se crea un pedido sin items aún. El test calcular_conNull
     * verifica exactamente este caso.
     */
    public Double calculate(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }
}

package com.ecommerce.discount;

import com.ecommerce.entity.Customer;
import org.springframework.stereotype.Component;

/**
 * Caso por defecto: no se aplica ningún descuento. El total no cambia.
 *
 * Esta clase implementa el Patrón Null Object.
 * Pregunten: ¿qué pasaría si en lugar de esta clase devolviéramos null
 * cuando ningún descuento aplica?
 *
 * En OrderService tendríamos que escribir:
 *   DiscountStrategy strategy = buscarEstrategia(customer);
 *   if (strategy != null) {                // null check obligatorio
 *       total = strategy.apply(total);
 *   }
 *
 * Y si alguien olvida el null check, hay un NullPointerException
 * en producción. El Patrón Null Object elimina esa posibilidad:
 * siempre hay una estrategia disponible, aunque sea una que
 * no hace nada. El flujo del código es uniforme, sin ramificaciones.
 *
 * isApplicable() devuelve siempre true para que esta clase actúe
 * como "el último recurso": si ninguna otra estrategia aplica,
 * esta siempre aplica y devuelve el total sin cambios.
 *
 * En DiscountStrategyFactory, esta clase va AL FINAL de la lista
 * exactamente por esa razón.
 */
@Component
public class NoDiscount implements DiscountStrategy {

    @Override
    public Double apply(Double total) {
        return total; // Identidad matemática: devuelve exactamente lo que recibe
    }

    @Override
    public boolean isApplicable(Customer customer) {
        return true; // Siempre aplica — es el caso por defecto
    }
}

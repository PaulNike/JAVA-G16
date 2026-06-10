package com.ecommerce.discount;

import com.ecommerce.entity.Customer;

import java.util.List;

/**
 * Factory estático para obtener la estrategia de descuento correcta.
 *
 * Aquí hay una diferencia conceptual importante que quiero que noten:
 *
 * En OrderService inyectamos List<DiscountStrategy> mediante Spring.
 * Spring detecta automáticamente todas las implementaciones y las inyecta.
 * Pero en esa forma, el ORDEN en que Spring las pone en la lista depende
 * del contexto de la aplicación y puede variar.
 *
 * Este Factory resuelve ese problema con control explícito: nosotros
 * definimos la lista con el orden correcto (VIP primero, nuevo cliente
 * segundo, sin descuento al final). Nadie puede inyectar accidentalmente
 * una nueva estrategia en el lugar equivocado.
 *
 * ¿Cuándo usar este Factory estático vs la inyección de List<> en Spring?
 *
 * Usen el Factory estático cuando:
 *   - Necesitan control total sobre el orden de evaluación
 *   - Las estrategias no tienen dependencias propias de Spring
 *   - Quieren usarlo fuera del contexto de Spring (scripts, utilidades)
 *
 * Usen la inyección de List<> en Spring cuando:
 *   - Las estrategias son beans de Spring con sus propias dependencias
 *   - El orden no importa o se puede controlar con @Order
 *
 * El constructor privado evita que alguien haga new DiscountStrategyFactory().
 * Este patrón de "clase de utilidad con solo métodos estáticos" es muy
 * común en Java (piensen en java.util.Collections o Arrays).
 */
public class DiscountStrategyFactory {

    // Orden de evaluación: VIP > primer pedido > sin descuento (caso base)
    private static final List<DiscountStrategy> STRATEGIES = List.of(
            new VipDiscount(),
            new NewCustomerDiscount(),
            new NoDiscount()
    );

    /**
     * Retorna la primera estrategia aplicable para el cliente.
     * Gracias a NoDiscount, este método nunca retorna null.
     */
    public static DiscountStrategy getFor(Customer customer) {
        return STRATEGIES.stream()
                .filter(strategy -> strategy.isApplicable(customer))
                .findFirst()
                .orElse(new NoDiscount());
    }

    private DiscountStrategyFactory() {
        // Clase de utilidad: no se instancia
    }
}

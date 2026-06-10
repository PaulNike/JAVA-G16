package com.ecommerce.discount;

import com.ecommerce.entity.Customer;

/**
 * Sesión 1 — Principio Abierto/Cerrado (O de SOLID) + Strategy Pattern
 *
 * Imaginemos que el product owner llega y dice: "queremos dar descuentos".
 * Un desarrollador sin experiencia haría esto en OrderService:
 *
 *   if (customer.isVip()) {
 *       total = total * 0.90;
 *   } else if (customer.getOrders().isEmpty()) {
 *       total = total * 0.95;
 *   }
 *
 * Al mes siguiente: "ahora queremos descuento para cumpleaños".
 * El desarrollador modifica ese mismo if. Al otro mes: "descuento
 * por volumen de compras". Otro if. Así, createOrder() se convierte
 * en un método de 200 líneas que nadie quiere tocar.
 *
 * El Principio Abierto/Cerrado dice:
 *   "El código debe estar ABIERTO para extensión,
 *    pero CERRADO para modificación."
 *
 * Esta interfaz ES el mecanismo de extensión. Cada tipo de descuento
 * es una clase nueva que implementa DiscountStrategy. OrderService
 * nunca se modifica. Para agregar "descuento cumpleaños": creamos
 * BirthdayDiscount, la anotamos con @Component, y Spring la registra
 * automáticamente. Sin tocar nada más.
 *
 * Esto también es el patrón GoF Strategy: una familia de algoritmos
 * intercambiables (los descuentos) detrás de una interfaz común.
 * El cliente (OrderService) usa el algoritmo sin saber cuál es.
 */
public interface DiscountStrategy {

    /**
     * Aplica el descuento al total bruto del pedido.
     * Las implementaciones deciden el porcentaje o la lógica.
     */
    Double apply(Double total);

    /**
     * Indica si este descuento aplica para el cliente dado.
     * OrderService llama a este método para encontrar la primera
     * estrategia aplicable en la lista de todas las registradas.
     */
    boolean isApplicable(Customer customer);
}

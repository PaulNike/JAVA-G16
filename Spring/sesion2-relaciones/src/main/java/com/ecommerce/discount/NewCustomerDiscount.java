package com.ecommerce.discount;

import com.ecommerce.entity.Customer;
import org.springframework.stereotype.Component;

/**
 * Descuento del 5% para clientes que realizan su primera compra.
 *
 * La lógica de "cliente nuevo" es: su lista de órdenes está vacía
 * al momento de crear el pedido. Como el nuevo pedido aún no fue
 * guardado en base de datos cuando OrderService evalúa los descuentos,
 * customer.getOrders().isEmpty() devuelve true correctamente para
 * alguien que nunca ha comprado antes.
 *
 * Una decisión de diseño importante: si un cliente VIP hace su primera
 * compra, ¿qué descuento aplica? Ninguno se acumula. OrderService toma
 * la PRIMERA estrategia aplicable de la lista, que es VipDiscount.
 * Este descuento no se evalúa en ese caso. Esa es una decisión de
 * negocio que encapsulamos en el orden de la lista, no en código
 * condicional dentro de las clases de descuento.
 *
 * Observen que esta clase no sabe nada de VipDiscount. El desacoplamiento
 * es total: cada estrategia es independiente de las demás.
 */
@Component
public class NewCustomerDiscount implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.05; // 5%

    @Override
    public Double apply(Double total) {
        return total * (1.0 - DISCOUNT_RATE);
    }

    @Override
    public boolean isApplicable(Customer customer) {
        // Un cliente "nuevo" es aquel que no tiene ningún pedido previo.
        return customer.getOrders() == null || customer.getOrders().isEmpty();
    }
}

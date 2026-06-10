package com.ecommerce.discount;

import com.ecommerce.entity.Customer;
import org.springframework.stereotype.Component;

/**
 * Descuento del 10% para clientes VIP.
 *
 * Noten algo importante: para agregar este nuevo descuento al sistema,
 * solo creamos esta clase. No tocamos OrderService, no tocamos
 * DiscountStrategy, no tocamos ninguna otra clase existente.
 *
 * Eso es exactamente lo que promete el Principio Abierto/Cerrado:
 * extendemos el comportamiento del sistema sin modificar
 * el código que ya funciona y que ya está testeado.
 *
 * El campo isVip en Customer se agregó precisamente para esta clase.
 * Por defecto vale false, así que ningún cliente existente se ve
 * afectado por el cambio.
 *
 * @Component hace que Spring registre esta clase como un bean y la
 * incluya automáticamente cuando alguien inyecta List<DiscountStrategy>.
 * No necesitamos registrarla manualmente en ningún lado.
 */
@Component
public class VipDiscount implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.10; // 10%

    @Override
    public Double apply(Double total) {
        return total * (1.0 - DISCOUNT_RATE);
    }

    @Override
    public boolean isApplicable(Customer customer) {
        return customer.isVip();
    }
}

package com.codigo.strategy;

import org.springframework.stereotype.Component;

@Component
public class NuevoClienteDescuento implements DescuentoStrategy {
    @Override
    public double aplicar(double total) {
        return total * 0.95;  // 5% descuento
    }

    @Override
    public String nombre() {
        return "Cliente Nuevo - 5% de Descuento";
    }
}

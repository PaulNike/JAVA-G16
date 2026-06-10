package com.codigo.strategy;

import org.springframework.stereotype.Component;

@Component
public class VipDescuento implements DescuentoStrategy {
    @Override
    public double aplicar(double total) {
        return total * 0.90;  // 10% off
    }

    @Override
    public String nombre() {
        return "VIP - 10% descuento";
    }
}

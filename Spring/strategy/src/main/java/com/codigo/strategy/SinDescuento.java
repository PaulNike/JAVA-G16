package com.codigo.strategy;

import org.springframework.stereotype.Component;

@Component
public class SinDescuento implements DescuentoStrategy{
    @Override
    public double aplicar(double total) {
        return total;
    }

    @Override
    public String nombre() {
        return "Sin Descuento";
    }
}

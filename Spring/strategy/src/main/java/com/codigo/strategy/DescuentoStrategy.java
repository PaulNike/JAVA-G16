package com.codigo.strategy;

public interface DescuentoStrategy {
    double aplicar(double total);
    String nombre();
}

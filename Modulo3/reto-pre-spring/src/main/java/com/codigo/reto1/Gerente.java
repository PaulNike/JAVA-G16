package com.codigo.reto1;

public class Gerente extends Empleado{

    private double bonoJefe; //Campo solo existe en Gerente

    public Gerente(String nombre, double sueldoBase, double bonoJefe) {
        super(nombre, sueldoBase);
        this.bonoJefe = bonoJefe;
    }

    @Override
    public double calcularBono() {
        return bonoJefe; //Bono de Jefe fijo
    }
}

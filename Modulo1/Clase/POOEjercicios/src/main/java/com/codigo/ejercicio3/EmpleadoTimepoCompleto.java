package com.codigo.ejercicio3;

public class EmpleadoTimepoCompleto extends Empleado{

    private double salarioFijo;

    public EmpleadoTimepoCompleto(String nombre, double salarioFijo) {
        super(nombre);
        this.salarioFijo = salarioFijo;
    }

    @Override
    public double calcularSalario() {
        return salarioFijo;
    }
}

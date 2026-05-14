package com.codigo.reto1;

public class Desarrollador extends Empleado{

    private double bonoPorProyecto;

    public Desarrollador(String nombre, double sueldoBase, double bonoPorProyecto) {
        super(nombre, sueldoBase);
        this.bonoPorProyecto = bonoPorProyecto;
    }

    @Override
    public double calcularBono() {
        return (sueldoBase * 0.10) + bonoPorProyecto;
    }
}

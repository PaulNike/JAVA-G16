package com.codigo.reto1;

public abstract class Empleado {
    //Campo protegido: visible en esta clase y en las subclases
    //pero no desde fuera del paquete. (Esta es la diferencia con el private)
    protected String nombre;
    protected double sueldoBase;

    //Constructor: incializa los campos comunes a todo empleado.
    //Las subaclases lo invocan con super(nombre, sueldo).
    public Empleado(String nombre, double sueldoBase) {
        this.nombre = nombre;
        this.sueldoBase = sueldoBase;
    }

    //Metodo abstracto: Obliga a TODAS las clases hijas a implementarlo.
    // No tienen cuerpo (ni llaves {})
    public abstract double calcularBono();

    //Metodo concreto: ya tiene implementación. Las clases hijas lo heredan gratis.
    public double sueldoTotal(){
        return sueldoBase + calcularBono(); //el bono es POLIMORFICO
    }

    @Override
    public String toString() {
        return nombre + " -> total: $ " + sueldoTotal();
    }
}

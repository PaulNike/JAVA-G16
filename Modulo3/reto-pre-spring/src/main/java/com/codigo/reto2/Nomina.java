package com.codigo.reto2;

public class Nomina implements Pagable{
    private String empleado;
    private  double sueldoBase;
    private  double bono;

    public Nomina(String empleado, double sueldoBase, double bono) {
        this.empleado = empleado;
        this.sueldoBase = sueldoBase;
        this.bono = bono;
    }

    @Override
    public double calcularTotal() {
        return sueldoBase + bono;
    }
}

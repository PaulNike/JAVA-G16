package com.codigo.ejercicio3;

public abstract class Empleado {

    protected String nombre;
    private static int totalEmpleados = 0;

    public Empleado(String nombre) {
        this.nombre = nombre;
        //Cada vez que se crea un empleado, aumenta el contador
        totalEmpleados++;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract double calcularSalario();

    public static double calcularTotalSalarios(Empleado[] empleados){
        double total = 0;
        for (Empleado e : empleados){ //Tipo Variable : collecion
            total += e.calcularSalario();
        }
        return total;
    }

    public static int getTotalEmpleados() {
        return totalEmpleados;
    }
}

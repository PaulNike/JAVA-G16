package com.codigo;

import com.codigo.ejercicio3.Empleado;
import com.codigo.ejercicio3.EmpleadoPorHoras;
import com.codigo.ejercicio3.EmpleadoTimepoCompleto;
import com.codigo.ejercicio5.PagoTarjeta;
import com.codigo.ejercicio5.PagoTransferencia;
import com.codigo.ejercicio5.PagoYape;
import com.codigo.ejercicio5.Pagos;

public class Main {
    public static void main(String[] args) {

        Empleado empleado1 = new EmpleadoTimepoCompleto("Paul", 3500);
        Empleado empleado2 = new EmpleadoPorHoras("Esteban", 48, 190);


        System.out.println("Empleado1: " + empleado1.getNombre());
        System.out.println("Salario: " + empleado1.calcularSalario());

        System.out.println();

        System.out.println("Empleado2: " + empleado2.getNombre());
        System.out.println("Salario: " + empleado2.calcularSalario());

        System.out.println();

        Empleado[] empleados = {empleado1,empleado2};
        double total = Empleado.calcularTotalSalarios(empleados);
        System.out.println(Constants.TOTAL_SALARIOS + total);

        System.out.println();
        System.out.println("Total Empleados: " + Empleado.getTotalEmpleados());

       //NombreClase nombreObjeto = new NombreClase();
       //nombreObjeto.metodo();

       //NombreClase.metodo();


        System.out.println();
        Pagos pago1 = new PagoYape();
        Pagos pago2 = new PagoTarjeta();
        Pagos pago3 = new PagoTransferencia();

        pago1.procesarPago(100);
        pago2.procesarPago(200);
        pago3.procesarPago(300);

    }
}

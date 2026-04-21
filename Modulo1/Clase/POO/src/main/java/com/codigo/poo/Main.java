package com.codigo.poo;

import com.codigo.poo.interfaces.RegistrarAutoImpl;
import com.codigo.poo.interfaces.RegistrarVehiculo;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hola Mundo");


        RegistrarVehiculo registrarVehiculo = new RegistrarAutoImpl();

        Auto auto1 = new Auto();
        //auto1.setMarca("Geely");
        //auto1.setAnio(2025);
        //auto1.setCaja("CVT");
        System.out.println(registrarVehiculo.registrarAuto(auto1));
        System.out.println(auto1.toString());
    }
}

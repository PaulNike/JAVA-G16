package com.codigo.modelos;

import com.codigo.abstractas.Vehiculo;
import com.codigo.interfaces.Encendible;

public class Auto extends Vehiculo{

    private int cantidadPuertas;

    public Auto(String marca, String modelo, int cantidadPuertas) {
        super(marca, modelo);
        this.cantidadPuertas = cantidadPuertas;
    }

    public int getCantidadPuertas() {
        return cantidadPuertas;
    }

    public void setCantidadPuertas(int cantidadPuertas) {
        if (cantidadPuertas > 0){
            this.cantidadPuertas = cantidadPuertas;
        }
    }
    @Override
    public void encender() {
        System.out.println("El auto ha sido encendido con llave o boton");
    }

    @Override
    public void apagar() {
        System.out.println("El auto ha sido apagado");
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("=== INFORMACION DEL AUTO ===");
        System.out.println("Marca: " + getMarca());
        System.out.println("Modelo: " + getModelo());
        System.out.println("Puertas : " + getCantidadPuertas());
        System.out.println("Velocidad Actual : " + getVelocidad() + " km/h");
    }

}

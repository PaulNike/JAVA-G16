package com.codigo.modelos;

import com.codigo.abstractas.Vehiculo;

public class Moto extends Vehiculo {

    private boolean tieneCascoGuardado;

    public Moto(String marca, String modelo, boolean tieneCascoGuardado) {
        super(marca, modelo);
        this.tieneCascoGuardado = tieneCascoGuardado;
    }

    public boolean isTieneCascoGuardado() {
        return tieneCascoGuardado;
    }

    public void setTieneCascoGuardado(boolean tieneCascoGuardado) {
        this.tieneCascoGuardado = tieneCascoGuardado;
    }

    @Override
    public void encender() {
        System.out.println("La moto ha sido encendida con arranque");
    }

    @Override
    public void apagar() {
        System.out.println("La Moto ha sido apagada");
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("=== INFORMACION DE LA MOTO ===");
        System.out.println("Marca: " + getMarca());
        System.out.println("Modelo: " + getModelo());
        System.out.println("Tiene Casco Guardado : " + tieneCascoGuardado);
        System.out.println("Velocidad Actual : " + getVelocidad() + " km/h");
    }
}

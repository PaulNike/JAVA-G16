package com.codigo.abstractas;

import com.codigo.interfaces.Encendible;

public abstract class Vehiculo implements Encendible {
    private String marca;
    private String modelo;
    private int velocidad;

    public Vehiculo(String marca, String modelo) {
        this.marca = marca;
        this.modelo = modelo;
        this.velocidad = 0;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        if(velocidad >= 0){
            this.velocidad = velocidad;
        }
    }

    public void acelerar(int incremento){
        if (incremento > 0) {  //-15  + 10= -5
            velocidad += incremento;
            if (velocidad < 0) {
                velocidad = 0;
                System.out.println("El vehiculo freno. Velocidad actual: " + velocidad + " km/h");
            }
        }
    }

    public void frenar(int decremento){
        if (decremento > 0) {  //-15  + 10= -5
            velocidad -= decremento;
            if (velocidad < 0) {
                velocidad = 0;
                System.out.println("El vehiculo freno. Velocidad actual: " + velocidad + " km/h");
            }
        }
    }

    public abstract void mostrarInformacion();

}

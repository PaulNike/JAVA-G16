package com.codigo.poo;

public class Auto {
    private String marca;
    private int anio;
    private String caja;
    private Moto moto;



    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getCaja() {
        return caja;
    }

    public void setCaja(String caja) {
        this.caja = caja;
    }

    @Override
    public String toString() {
        return "Auto{" +
                "marca='" + marca + '\'' +
                ", anio=" + anio +
                ", caja='" + caja + '\'' +
                '}';
    }
}

package com.codigo.reto2;

public class Factura implements Pagable{

    private String numeroFactura;
    private double subtotal;
    private double igv;

    public Factura(String numeroFactura, double subtotal, double igv) {
        this.numeroFactura = numeroFactura;
        this.subtotal = subtotal;
        this.igv = igv;
    }

    @Override
    public double calcularTotal() {
        return subtotal + igv;
    }
}

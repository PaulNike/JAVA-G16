package com.codigo.ejercicio5;

public class PagoYape implements Pagos {

    @Override
    public void procesarPago(double monto) {
        System.out.println("Pago de " + monto + " realizado con Yape");
    }
}

package com.codigo.ejercicio5;

public class PagoTransferencia implements Pagos {

    @Override
    public void procesarPago(double monto) {
        System.out.println("Pago de " + monto + " realizado con Transferencia");
    }
}

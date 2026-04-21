package com.codigo.ejercicio1;

public class CuentaBancaria {

    private String titular;
    private double saldo;

    public CuentaBancaria(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
            System.out.println("Depósito realizado: " + monto);
        } else {
            System.out.println("Monto inválido para depósito");
        }
    }

    public void retirar(double monto) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            System.out.println("Retiro realizado: " + monto);
        } else {
            System.out.println("No se puede retirar ese monto");
        }
    }

    public void mostrarSaldo() {
        System.out.println("Saldo actual: " + saldo);
    }
}
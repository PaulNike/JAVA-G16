package com.codigo.ejercicio2;

public class Producto {

    private String nombre;
    private double precio;
    private int stock;

    public Producto(String nombre, double precio, int stockInicial) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stockInicial;
    }

    public void vender(int cantidad) {
        if (cantidad > 0 && cantidad <= stock) {
            stock -= cantidad;
            System.out.println("Venta realizada: " + cantidad + " unidades");
            System.out.println("Stock actual: " + stock);
        } else {
            System.out.println("No hay suficiente stock");
        }
        System.out.println("=======================================");
    }

    public void reponer(int cantidad) {
        if (cantidad > 0) {
            stock += cantidad;
            System.out.println("Stock repuesto: " + cantidad + " unidades");
            System.out.println("Stock actual: " + stock);
        } else {
            System.out.println("Cantidad inválida para reponer");
        }
        System.out.println("=======================================");
    }

    public void mostrarEstado() {
        System.out.println("==================MOSTRAR ESTADO=====================");
        System.out.println("Producto: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Stock actual: " + stock);
    }
}

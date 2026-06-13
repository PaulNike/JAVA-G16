package com.codigo.unit_testing.service;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class CalculadoraService {
    public int sumar(int a, int b){
        return a + b;
    }
    public int restar(int a, int b){
        return a - b;
    }
    public int dividir(int a, int b){
        if(b == 0){
            throw new ArithmeticException("No se puede dividir entre 0");
        }
        return a/b;
    }

    public boolean esPar(int numero){
        return numero % 2 == 0;
    }
    public boolean estaEnRango(int numero, int min, int max){
        return numero >= min && numero <= max;
    }

    public String obtenerNombre(String nombre){
        return nombre.toUpperCase();
    }

    public void mostrarResultado(int a){
        log.info("El resultado es: " + a);
    }
}

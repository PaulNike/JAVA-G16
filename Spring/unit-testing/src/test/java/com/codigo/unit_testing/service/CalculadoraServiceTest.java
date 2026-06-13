package com.codigo.unit_testing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraServiceTest {

    private CalculadoraService calculadoraService;

    @BeforeEach
    void setUp(){
        calculadoraService = new CalculadoraService();
    }

    @Test
    void testSumarHappyPath(){

        //ARRANGE
        int a = 2;
        int b = 3;

        //ACT
        int resultado = calculadoraService.sumar(a, b);

        //ASSERT
        assertEquals(5, resultado, "El valor obtenido: "
                + resultado + " no es igual al esperado" );
    }

    @Test
    void testRestarHappyPath(){

        //ARRANGE
        int a = 2;
        int b = 3;

        //ACT
        int resultado = calculadoraService.restar(a, b);

        //ASSERT
        assertEquals(-1, resultado, "El valor obtenido: "
                + resultado + " no es igual al esperado" );
    }

    @Test
    void testDividirHappyPath(){
        int a = 10;
        int b = 2;
        int resultado = calculadoraService.dividir(a, b);
        assertEquals(5, resultado, "El valor obtenido: "
                + resultado + " no es igual al esperado" );
    }

    @Test
    void testDividirError(){
        int a = 2;
        int b = 0;

        //act + assert
        assertThrows(ArithmeticException.class, () -> calculadoraService.dividir(a, b));
    }

}
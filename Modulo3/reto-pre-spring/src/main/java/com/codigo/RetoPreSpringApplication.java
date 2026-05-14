package com.codigo;

import com.codigo.reto1.Desarrollador;
import com.codigo.reto1.Empleado;
import com.codigo.reto1.Gerente;
import com.codigo.reto2.Factura;
import com.codigo.reto2.Nomina;
import com.codigo.reto2.Pagable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetoPreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetoPreSpringApplication.class, args);
		System.out.println("****************** INICIO RETO 1 **********************");
		Empleado ger = new Gerente("Ivonne", 3000, 500);
		Empleado des = new Desarrollador("Daniel", 2500, 300);

		System.out.println(ger);
		System.out.println(des);
		System.out.println("****************** FIN RETO 1 **********************");

		System.out.println("****************** INICIO RETO 2 **********************");
		Pagable factura = new Factura("F001-123", 1500.00, 270.00);
		Pagable nomina = new Nomina("Nike Rodriguez", 2500.00, 500.00);

		System.out.println( "Total de la Factura : S/. " + factura.calcularTotal());
		System.out.println( "Total de la Nomina : S/. " + nomina.calcularTotal());
		System.out.println("****************** FIN RETO 2 **********************");

	}

}

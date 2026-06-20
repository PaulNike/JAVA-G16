package pe.codigo.reniec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal: el punto de arranque de la aplicacion.
 *
 * La anotacion @EnableFeignClients le dice a Spring que escanee
 * el proyecto buscando interfaces anotadas con @FeignClient y genere
 * automaticamente su implementacion (un "proxy") en tiempo de ejecucion.
 * Sin esta anotacion, nuestro ReniecFeignClient nunca se inyectaria.
 */
@EnableFeignClients
@SpringBootApplication
public class ReniecFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReniecFeignApplication.class, args);
    }
}

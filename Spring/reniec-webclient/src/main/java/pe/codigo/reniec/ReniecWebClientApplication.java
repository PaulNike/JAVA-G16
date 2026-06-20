package pe.codigo.reniec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aca arranca toda la aplicacion.
 *
 * Comparen con el proyecto de Feign: alla teniamos @EnableFeignClients.
 * Aqui no hace falta nada de eso, porque WebClient no se "activa" con una
 * anotacion: es simplemente un objeto que vamos a construir nosotros mismos
 * en la clase WebClientConfig.
 */
@SpringBootApplication
public class ReniecWebClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReniecWebClientApplication.class, args);
    }
}

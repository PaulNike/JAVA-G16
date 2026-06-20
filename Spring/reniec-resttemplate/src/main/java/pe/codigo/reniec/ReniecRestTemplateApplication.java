package pe.codigo.reniec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aca arranca toda la aplicacion.
 *
 * Igual que con WebClient, no necesitamos ninguna anotacion especial: el
 * RestTemplate lo vamos a construir nosotros mismos en RestTemplateConfig.
 * Comparen los tres "main": Feign necesitaba @EnableFeignClients, WebClient y
 * RestTemplate no necesitan nada extra.
 */
@SpringBootApplication
public class ReniecRestTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReniecRestTemplateApplication.class, args);
    }
}

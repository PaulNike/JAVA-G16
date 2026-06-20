package pe.codigo.reniec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aca arranca toda la aplicacion.
 *
 * Igual que con WebClient y RestTemplate, no necesitamos una anotacion especial.
 * Retrofit es una libreria aparte: la vamos a construir a mano en RetrofitConfig
 * y registrarla como un bean para poder inyectarla.
 */
@SpringBootApplication
public class ReniecRetrofitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReniecRetrofitApplication.class, args);
    }
}

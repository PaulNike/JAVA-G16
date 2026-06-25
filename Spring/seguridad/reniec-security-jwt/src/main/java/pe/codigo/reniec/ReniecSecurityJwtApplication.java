package pe.codigo.reniec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de arranque de la aplicacion.
 *
 * Es el MISMO proyecto que consume RENIEC con cache en 3 niveles. La unica
 * novedad es que ahora tiene Spring Security encima. Fijense que aqui no hace
 * falta ninguna anotacion nueva: con tener la dependencia de security en el
 * pom y la clase SecurityConfig, la proteccion ya queda activada.
 */
@SpringBootApplication
public class ReniecSecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReniecSecurityJwtApplication.class, args);
    }
}

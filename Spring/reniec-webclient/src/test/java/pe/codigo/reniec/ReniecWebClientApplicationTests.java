package pe.codigo.reniec;

import org.junit.jupiter.api.Test;

/**
 * Dejamos este test "vacio" a proposito. Un @SpringBootTest normal intenta
 * levantar todo el contexto (incluidos Redis y Postgres), y eso fallaria en
 * una maquina que no los tenga corriendo. Para nuestra clase, lo que importa
 * por ahora es que el proyecto COMPILE. Mas adelante, cuando veamos testing
 * con Testcontainers, reactivamos el arranque del contexto completo.
 */
class ReniecWebClientApplicationTests {

    @Test
    void elProyectoCompila() {
        // Si el codigo compila, esta prueba pasa.
    }
}

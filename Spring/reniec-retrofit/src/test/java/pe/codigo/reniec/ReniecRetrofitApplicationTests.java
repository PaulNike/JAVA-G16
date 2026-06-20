package pe.codigo.reniec;

import org.junit.jupiter.api.Test;

/**
 * Test "vacio" a proposito. Un @SpringBootTest normal intentaria levantar todo
 * el contexto (Redis y Postgres incluidos) y fallaria en una maquina que no los
 * tenga corriendo. Por ahora lo que nos importa es que el proyecto COMPILE.
 * Cuando veamos Testcontainers, reactivamos el arranque del contexto.
 */
class ReniecRetrofitApplicationTests {

    @Test
    void elProyectoCompila() {
        // Si el codigo compila, esta prueba pasa.
    }
}

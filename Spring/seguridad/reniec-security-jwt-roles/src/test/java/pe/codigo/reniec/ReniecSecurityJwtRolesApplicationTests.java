package pe.codigo.reniec;

import org.junit.jupiter.api.Test;

/**
 * Test "vacio" a proposito. Un @SpringBootTest normal levantaria todo el
 * contexto (Redis y Postgres incluidos) y fallaria en una maquina sin ellos.
 * Por ahora basta con que el proyecto COMPILE. Cuando veamos testing de
 * seguridad, usaremos spring-security-test (@WithMockUser, etc.).
 */
class ReniecSecurityJwtRolesApplicationTests {

    @Test
    void elProyectoCompila() {
    }
}

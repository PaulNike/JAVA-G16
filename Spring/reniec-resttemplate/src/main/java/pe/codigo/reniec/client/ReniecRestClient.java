package pe.codigo.reniec.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Este es el cliente que habla con RENIEC usando RestTemplate.
 *
 * Comparen con los otros dos:
 *  - Feign era una interface sin codigo (Spring generaba todo).
 *  - WebClient devolvia un Mono y tocaba hacer .block().
 *  - RestTemplate es directo y bloqueante: getForObject() hace la peticion,
 *    convierte el JSON al objeto y nos lo devuelve, todo en una sola linea.
 *
 * El "{numero}" es un placeholder: RestTemplate reemplaza ahi el valor que le
 * pasamos como ultimo argumento. Esto, ademas de ser comodo, evita problemas
 * si el valor trajera caracteres raros (lo codifica por nosotros).
 */
@Component
@RequiredArgsConstructor
public class ReniecRestClient {

    // Spring inyecta el bean que creamos en RestTemplateConfig.
    private final RestTemplate reniecRestTemplate;

    public PersonaResponse consultarDni(String numero) {
        return reniecRestTemplate.getForObject(
                "/v1/reniec/dni?numero={numero}",
                PersonaResponse.class,
                numero);
    }
}

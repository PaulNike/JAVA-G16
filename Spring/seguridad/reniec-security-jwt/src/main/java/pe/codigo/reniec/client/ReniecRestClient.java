package pe.codigo.reniec.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Cliente que habla con RENIEC usando RestTemplate.
 *
 * getForObject() hace la peticion, convierte el JSON al objeto y nos lo
 * devuelve en una sola linea. El "{numero}" es un placeholder que RestTemplate
 * reemplaza por el valor que le pasamos como ultimo argumento.
 */
@Component
@RequiredArgsConstructor
public class ReniecRestClient {

    private final RestTemplate reniecRestTemplate;

    public PersonaResponse consultarDni(String numero) {
        return reniecRestTemplate.getForObject(
                "/v1/reniec/dni?numero={numero}",
                PersonaResponse.class,
                numero);
    }
}

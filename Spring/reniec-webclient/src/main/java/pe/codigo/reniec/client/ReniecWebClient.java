package pe.codigo.reniec.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Esta clase es el unico cambio grande respecto al proyecto con Feign.
 * Miren bien como se arma una llamada con WebClient, leyendola como una frase:
 *
 *   get()        -> voy a hacer un GET
 *   uri(...)     -> a esta ruta, con el parametro 'numero'
 *   retrieve()   -> mandalo y traeme la respuesta
 *   bodyToMono() -> convierte el cuerpo (JSON) a un PersonaResponse
 *   block()      -> espera aqui hasta tener el resultado
 *
 * El detalle clave es block(). WebClient es reactivo: bodyToMono nos devuelve
 * un Mono, que es como una "promesa" de un valor futuro. Como el resto de
 * nuestra app es bloqueante (Redis y JPA lo son), usamos block() para esperar
 * el dato aqui mismo y seguir con el flujo normal de siempre.
 *
 * Otra cosa importante: retrieve() lanza una WebClientResponseException de
 * forma automatica cuando RENIEC responde 4xx o 5xx. Esa excepcion la vamos
 * a atrapar y traducir en el service, no aqui.
 */
@Component
@RequiredArgsConstructor
public class ReniecWebClient {

    // Spring inyecta el bean que creamos en WebClientConfig.
    // El nombre del campo coincide con el del @Bean ("reniecWebClient").
    private final WebClient reniecWebClient;

    public PersonaResponse consultarDni(String numero) {
        return reniecWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/reniec/dni")
                        .queryParam("numero", numero)
                        .build())
                .retrieve()
                .bodyToMono(PersonaResponse.class)
                .block();
    }
}

package pe.codigo.reniec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Aqui construimos el WebClient una sola vez y lo dejamos listo para inyectar.
 *
 * Comparen con el FeignConfig del otro proyecto: alla pusimos un interceptor
 * para meter el token. Aqui es mas directo todavia: armamos el WebClient con
 * su baseUrl y sus headers por defecto. El token "Bearer ..." y el
 * Content-Type quedan pegados a TODAS las peticiones que haga este cliente,
 * sin que tengamos que repetirlos en cada llamada.
 *
 * El WebClient.Builder que recibimos como parametro nos lo da Spring Boot
 * automaticamente (gracias a que agregamos spring-boot-starter-webflux).
 */
@Configuration
public class WebClientConfig {

    @Value("${reniec.api.url}")
    private String baseUrl;

    @Value("${reniec.api.token}")
    private String token;

    @Bean
    public WebClient reniecWebClientBean(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

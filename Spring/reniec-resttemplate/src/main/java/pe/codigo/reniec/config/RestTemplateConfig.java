package pe.codigo.reniec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Aqui construimos el RestTemplate una sola vez y lo dejamos listo para inyectar.
 *
 * Dos cosas importantes que hacemos con el RestTemplateBuilder (que nos lo da
 * Spring Boot solito):
 *
 *  1) rootUri(baseUrl): fija la URL base. Asi, al llamar, escribimos solo la
 *     ruta relativa "/v1/reniec/dni" y RestTemplate la pega al baseUrl.
 *     Fijense que WebClient tenia baseUrl(); aqui el equivalente es rootUri().
 *
 *  2) additionalInterceptors(...): un interceptor que se ejecuta ANTES de cada
 *     peticion para meter el header "Authorization: Bearer <token>" y el
 *     Content-Type. Es el mismo concepto del RequestInterceptor de Feign,
 *     solo que aqui se llama ClientHttpRequestInterceptor.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${reniec.api.url}")
    private String baseUrl;

    @Value("${reniec.api.token}")
    private String token;

    @Bean
    public RestTemplate reniecRestTemplateBean(RestTemplateBuilder builder) {
        return builder
                .rootUri(baseUrl)
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(token);
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return execution.execute(request, body);
                })
                .build();
    }
}

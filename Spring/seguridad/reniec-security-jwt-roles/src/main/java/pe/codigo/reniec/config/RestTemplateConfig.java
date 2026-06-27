package pe.codigo.reniec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Construimos el RestTemplate una sola vez con su URL base y el token.
 *
 * rootUri() fija la URL base, y additionalInterceptors() agrega el header
 * Authorization con el token en cada llamada al API externo. OJO: este token
 * es el de RENIEC (para SALIR a consultar), no tiene nada que ver con la
 * seguridad de NUESTRA API (la de entrada), que vive en SecurityConfig.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${reniec.api.url}")
    private String baseUrl;

    @Value("${reniec.api.token}")
    private String token;

    @Bean
    public RestTemplate reniecRestTemplate(RestTemplateBuilder builder) {
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

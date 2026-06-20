package pe.codigo.reniec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.codigo.reniec.config.FeignConfig;
import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Cliente Feign declarativo para consumir el API de RENIEC (decolecta).
 *
 * La magia de Feign es que SOLO declaramos esta interface.
 * No escribimos codigo de conexion HTTP: Spring genera la implementacion.
 *
 *  - name: nombre logico del cliente (debe ser unico en la app).
 *  - url: la leemos del application.properties con ${...}.
 *  - configuration: la clase donde inyectamos el token de autorizacion.
 */
@FeignClient(
        name = "reniecClient",
        url = "${reniec.api.url}",
        configuration = FeignConfig.class
)
public interface ReniecFeignClient {

    /**
     * Mapea a:
     *   GET https://api.decolecta.com/v1/reniec/dni?numero=46027897
     */
    @GetMapping("/v1/reniec/dni")
    PersonaResponse consultarDni(@RequestParam("numero") String numero);
}

package pe.codigo.reniec.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Configuracion del cliente Feign: aqui inyectamos el token de autorizacion.
 *
 * Un RequestInterceptor se ejecuta ANTES de cada peticion que hace
 * Feign. Lo usamos para agregar el header "Authorization: Bearer <token>"
 * automaticamente en TODAS las llamadas, sin repetirlo en cada metodo.
 *
 * OJO: a proposito NO ponemos @Configuration en esta clase. Si lo hicieramos,
 * el interceptor se aplicaria a todos los clientes Feign de la app. Al dejarla
 * "limpia" y referenciarla solo desde @FeignClient(configuration = ...),
 * limitamos el token unicamente a este cliente.
 */
public class FeignConfig {

    @Value("${reniec.api.token}")
    private String token;

    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            requestTemplate.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        };
    }
}

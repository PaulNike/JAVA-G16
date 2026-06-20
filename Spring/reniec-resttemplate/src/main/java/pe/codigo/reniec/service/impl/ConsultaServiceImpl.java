package pe.codigo.reniec.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import pe.codigo.reniec.client.ReniecRestClient;
import pe.codigo.reniec.dto.PersonaResponse;
import pe.codigo.reniec.entity.Persona;
import pe.codigo.reniec.exception.DniNoEncontradoException;
import pe.codigo.reniec.exception.ReniecApiException;
import pe.codigo.reniec.repository.PersonaRepository;
import pe.codigo.reniec.service.ConsultaService;

import java.time.Duration;
import java.util.Optional;

/**
 * Aqui vive el cerebro del proyecto: la logica de los 3 niveles.
 *
 * Lean el metodo consultarPorDni de arriba hacia abajo: es exactamente el
 * mismo diagrama de siempre. Lo unico que cambio respecto a Feign y WebClient
 * es de quien dependemos para llamar a RENIEC (ReniecRestClient) y que
 * excepciones atrapamos.
 */
@Slf4j                       // Lombok nos crea el objeto 'log' solito
@Service                     // marca la clase como un bean de servicio
@RequiredArgsConstructor     // genera el constructor con los campos 'final'
public class ConsultaServiceImpl implements ConsultaService {

    private final RedisTemplate<String, PersonaResponse> redisTemplate;
    private final PersonaRepository personaRepository;
    private final ReniecRestClient reniecRestClient;

    @Value("${reniec.cache.ttl-segundos}")
    private long ttlSegundos;

    private static final String PREFIJO_CACHE = "dni:";

    @Override
    public PersonaResponse consultarPorDni(String dni) {
        String clave = PREFIJO_CACHE + dni;

        // ---------- NIVEL 1: REDIS (cache en memoria) ----------
        PersonaResponse desdeCache = redisTemplate.opsForValue().get(clave);
        if (desdeCache != null) {
            log.info("DNI {} encontrado en REDIS (cache)", dni);
            return desdeCache;
        }

        // ---------- NIVEL 2: POSTGRESQL (base de datos) ----------
        Optional<Persona> desdeBd = personaRepository.findById(dni);
        if (desdeBd.isPresent()) {
            log.info("DNI {} encontrado en POSTGRESQL (bd)", dni);
            PersonaResponse response = mapearAResponse(desdeBd.get());
            guardarEnCache(clave, response);   // lo subimos al cache para la proxima vez
            return response;
        }

        // ---------- NIVEL 3: API EXTERNA RENIEC ----------
        log.info("DNI {} no esta en cache ni en bd. Consultando RENIEC...", dni);
        PersonaResponse response = consultarReniec(dni);

        // Guardamos lo que nos dio RENIEC en BD y en cache, para futuras consultas
        personaRepository.save(mapearAEntidad(response));
        guardarEnCache(clave, response);

        return response;
    }

    /**
     * Aqui aislamos la llamada al API externo y traducimos los errores tecnicos
     * a NUESTRAS excepciones de negocio. Fijense en algo lindo de RestTemplate:
     * separa dos tipos de problema muy distintos.
     *
     *  - HttpStatusCodeException: RENIEC SI respondio, pero con un codigo de
     *    error (404, 401, 500...). Sus subtipos NotFound y Unauthorized nos
     *    dejan distinguir cada caso de forma comoda.
     *
     *  - RestClientException (a secas): ni siquiera hubo respuesta. Se cayo la
     *    conexion, hubo timeout, no resolvio el DNS, etc. Es un problema de red,
     *    no del API.
     */
    private PersonaResponse consultarReniec(String dni) {
        try {
            PersonaResponse response = reniecRestClient.consultarDni(dni);

            // Defensa extra: si RENIEC responde 200 pero sin datos utiles
            if (response == null || response.getNumeroDocumento() == null) {
                throw new DniNoEncontradoException(dni);
            }
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            // 404 -> el DNI no existe en RENIEC
            log.warn("RENIEC respondio 404 para el DNI {}", dni);
            throw new DniNoEncontradoException(dni);

        } catch (HttpClientErrorException.Unauthorized e) {
            // 401 -> el token es invalido o ya vencio
            log.error("Token invalido al consultar RENIEC", e);
            throw new ReniecApiException("El token de acceso a RENIEC es invalido o expiro.");

        } catch (HttpStatusCodeException e) {
            // Cualquier otro codigo de error que devuelva RENIEC (400, 500, etc.)
            log.error("RENIEC respondio con error. Status: {}", e.getStatusCode(), e);
            throw new ReniecApiException(
                    "El servicio de RENIEC no esta disponible en este momento. Intente mas tarde.");

        } catch (RestClientException e) {
            // No hubo respuesta: timeout, conexion rechazada, DNS, etc.
            log.error("No se pudo conectar con RENIEC", e);
            throw new ReniecApiException(
                    "No se pudo conectar con el servicio de RENIEC. Intente mas tarde.");
        }
    }

    private void guardarEnCache(String clave, PersonaResponse response) {
        redisTemplate.opsForValue().set(clave, response, Duration.ofSeconds(ttlSegundos));
    }

    // ----- Metodos auxiliares para pasar de Entity a DTO y viceversa -----

    private Persona mapearAEntidad(PersonaResponse r) {
        return Persona.builder()
                .numeroDocumento(r.getNumeroDocumento())
                .nombres(r.getNombres())
                .apellidoPaterno(r.getApellidoPaterno())
                .apellidoMaterno(r.getApellidoMaterno())
                .nombreCompleto(r.getNombreCompleto())
                .build();
    }

    private PersonaResponse mapearAResponse(Persona p) {
        return PersonaResponse.builder()
                .numeroDocumento(p.getNumeroDocumento())
                .nombres(p.getNombres())
                .apellidoPaterno(p.getApellidoPaterno())
                .apellidoMaterno(p.getApellidoMaterno())
                .nombreCompleto(p.getNombreCompleto())
                .build();
    }
}

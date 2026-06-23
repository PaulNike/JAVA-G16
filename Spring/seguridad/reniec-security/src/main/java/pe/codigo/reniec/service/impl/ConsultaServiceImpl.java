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
 * El cerebro del proyecto: la logica de cache en 3 niveles.
 *
 * IMPORTANTE para la clase de seguridad: este archivo NO cambia ni una linea
 * respecto al proyecto anterior. La seguridad se agrega "por fuera"; la logica
 * de negocio ni se entera de que ahora hay que estar autenticado. Eso es bajo
 * acoplamiento: cada capa se ocupa de lo suyo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
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

        // NIVEL 1: REDIS (cache en memoria)
        PersonaResponse desdeCache = redisTemplate.opsForValue().get(clave);
        if (desdeCache != null) {
            log.info("DNI {} encontrado en REDIS (cache)", dni);
            return desdeCache;
        }

        // NIVEL 2: POSTGRESQL (base de datos)
        Optional<Persona> desdeBd = personaRepository.findById(dni);
        if (desdeBd.isPresent()) {
            log.info("DNI {} encontrado en POSTGRESQL (bd)", dni);
            PersonaResponse response = mapearAResponse(desdeBd.get());
            guardarEnCache(clave, response);
            return response;
        }

        // NIVEL 3: API EXTERNA RENIEC
        log.info("DNI {} no esta en cache ni en bd. Consultando RENIEC...", dni);
        PersonaResponse response = consultarReniec(dni);

        // back-fill: lo guardamos en BD y cache para futuras consultas
        personaRepository.save(mapearAEntidad(response));
        guardarEnCache(clave, response);

        return response;
    }

    private PersonaResponse consultarReniec(String dni) {
        try {
            PersonaResponse response = reniecRestClient.consultarDni(dni);
            if (response == null || response.getNumeroDocumento() == null) {
                throw new DniNoEncontradoException(dni);
            }
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            throw new DniNoEncontradoException(dni);
        } catch (HttpStatusCodeException e) {
            log.error("RENIEC respondio con error. Status: {}", e.getStatusCode(), e);
            throw new ReniecApiException(
                    "El servicio de RENIEC no esta disponible en este momento. Intente mas tarde.");
        } catch (RestClientException e) {
            log.error("No se pudo conectar con RENIEC", e);
            throw new ReniecApiException(
                    "No se pudo conectar con el servicio de RENIEC. Intente mas tarde.");
        }
    }

    private void guardarEnCache(String clave, PersonaResponse response) {
        redisTemplate.opsForValue().set(clave, response, Duration.ofSeconds(ttlSegundos));
    }

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

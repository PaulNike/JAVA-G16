package pe.codigo.reniec.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.codigo.reniec.client.ReniecWebClient;
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
 * mismo diagrama que dibujamos. Y es identico al del proyecto con Feign:
 * lo unico que cambio fue de quien dependemos para llamar a RENIEC
 * (ReniecWebClient) y que excepcion atrapamos (WebClientResponseException).
 */
@Slf4j                       // Lombok nos crea el objeto 'log' solito
@Service                     // marca la clase como un bean de servicio
@RequiredArgsConstructor     // genera el constructor con los campos 'final'
public class ConsultaServiceImpl implements ConsultaService {

    // Inyeccion por constructor (la forma recomendada en Spring).
    // @RequiredArgsConstructor arma el constructor con estos 3 campos final.
    private final RedisTemplate<String, PersonaResponse> redisTemplate;
    private final PersonaRepository personaRepository;
    private final ReniecWebClient reniecWebClient;

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
     * Aqui aislamos la llamada al API externo y, lo mas importante, traducimos
     * los errores tecnicos de WebClient a NUESTRAS excepciones de negocio.
     * El usuario nunca debe ver un error tecnico: siempre vera un mensaje claro.
     *
     * Recuerden: retrieve() ya lanza WebClientResponseException cuando RENIEC
     * responde con un codigo de error. Nosotros solo decidimos que hacer con
     * cada caso.
     */
    private PersonaResponse consultarReniec(String dni) {
        try {
            PersonaResponse response = reniecWebClient.consultarDni(dni);

            // Defensa extra: si RENIEC responde 200 pero sin datos utiles
            if (response == null || response.getNumeroDocumento() == null) {
                throw new DniNoEncontradoException(dni);
            }
            return response;

        } catch (WebClientResponseException.NotFound e) {
            // 404 -> el DNI no existe en RENIEC
            log.warn("RENIEC respondio 404 para el DNI {}", dni);
            throw new DniNoEncontradoException(dni);

        } catch (WebClientResponseException.Unauthorized e) {
            // 401 -> el token es invalido o ya vencio
            log.error("Token invalido al consultar RENIEC", e);
            throw new ReniecApiException("El token de acceso a RENIEC es invalido o expiro.");

        } catch (WebClientResponseException e) {
            // Cualquier otro error del API externo (500, timeout, etc.)
            log.error("Error consultando RENIEC. Status: {}", e.getStatusCode(), e);
            throw new ReniecApiException(
                    "El servicio de RENIEC no esta disponible en este momento. Intente mas tarde.");
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

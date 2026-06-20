package pe.codigo.reniec.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pe.codigo.reniec.client.ReniecFeignClient;
import pe.codigo.reniec.dto.PersonaResponse;
import pe.codigo.reniec.entity.Persona;
import pe.codigo.reniec.exception.DniNoEncontradoException;
import pe.codigo.reniec.exception.ReniecApiException;
import pe.codigo.reniec.repository.PersonaRepository;
import pe.codigo.reniec.service.ConsultaService;

import java.time.Duration;
import java.util.Optional;

/**
 * Implementacion del servicio: AQUI vive la logica de los 3 niveles.
 *
 * Este es el cerebro del proyecto. Lean el metodo consultarPorDni
 * de arriba hacia abajo: es exactamente el diagrama que vimos.
 */
@Slf4j                       // @Slf4j (Lombok) crea el objeto 'log' automaticamente
@Service                     // marca esta clase como un bean de servicio
@RequiredArgsConstructor     // genera el constructor con los campos 'final' (inyeccion)
public class ConsultaServiceImpl implements ConsultaService {

    // Inyeccion por constructor (la forma recomendada en Spring).
    // @RequiredArgsConstructor crea el constructor con estos 3 campos final.
    private final RedisTemplate<String, PersonaResponse> redisTemplate;
    private final PersonaRepository personaRepository;
    private final ReniecFeignClient reniecFeignClient;

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
            guardarEnCache(clave, response);   // lo subimos al cache para la proxima
            return response;
        }

        // ---------- NIVEL 3: API EXTERNA RENIEC ----------
        log.info("DNI {} no esta en cache ni en bd. Consultando RENIEC...", dni);
        PersonaResponse response = consultarReniec(dni);

        // Guardamos el resultado en BD y en cache para futuras consultas
        personaRepository.save(mapearAEntidad(response));
        guardarEnCache(clave, response);

        return response;
    }

    /**
     * Aqui aislamos la llamada al API externo y traducimos los
     * errores tecnicos de Feign a NUESTRAS excepciones de negocio. El usuario
     * nunca vera un error tecnico de Feign: vera nuestro mensaje claro.
     */
    private PersonaResponse consultarReniec(String dni) {
        try {
            PersonaResponse response = reniecFeignClient.consultarDni(dni);

            // Defensa extra: si el API responde 200 pero sin datos utiles
            if (response == null || response.getNumeroDocumento() == null) {
                throw new DniNoEncontradoException(dni);
            }
            return response;

        } catch (FeignException.NotFound e) {
            // 404 -> el DNI no existe en RENIEC
            log.warn("RENIEC respondio 404 para el DNI {}", dni);
            throw new DniNoEncontradoException(dni);

        } catch (FeignException.Unauthorized e) {
            // 401 -> token invalido o vencido
            log.error("Token invalido al consultar RENIEC", e);
            throw new ReniecApiException("El token de acceso a RENIEC es invalido o expiro.");

        } catch (FeignException e) {
            // Cualquier otro error del API externo (500, timeout, etc.)
            log.error("Error consultando RENIEC. Status: {}", e.status(), e);
            throw new ReniecApiException(
                    "El servicio de RENIEC no esta disponible en este momento. Intente mas tarde.");
        }
    }

    private void guardarEnCache(String clave, PersonaResponse response) {
        redisTemplate.opsForValue().set(clave, response, Duration.ofSeconds(ttlSegundos));
    }

    // ----- Metodos auxiliares de mapeo Entity <-> DTO -----

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

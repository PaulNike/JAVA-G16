package pe.codigo.reniec.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pe.codigo.reniec.client.ReniecRetrofitClient;
import pe.codigo.reniec.client.RetrofitCallException;
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
 * El metodo consultarPorDni es identico al de los otros tres proyectos. Lo unico
 * que cambia es a quien le pedimos los datos de RENIEC (ReniecRetrofitClient) y
 * como traducimos el error: ahora revisamos el codigo que nos paso el cliente
 * dentro de la RetrofitCallException.
 */
@Slf4j                       // Lombok nos crea el objeto 'log' solito
@Service                     // marca la clase como un bean de servicio
@RequiredArgsConstructor     // genera el constructor con los campos 'final'
public class ConsultaServiceImpl implements ConsultaService {

    private final RedisTemplate<String, PersonaResponse> redisTemplate;
    private final PersonaRepository personaRepository;
    private final ReniecRetrofitClient reniecRetrofitClient;

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
     * Traducimos el problema tecnico a NUESTRAS excepciones de negocio.
     * El cliente ya hizo el trabajo sucio de revisar el Response; aqui solo
     * decidimos el mensaje segun lo que nos cuenta la RetrofitCallException.
     */
    private PersonaResponse consultarReniec(String dni) {
        try {
            PersonaResponse response = reniecRetrofitClient.consultarDni(dni);

            // Defensa extra: si RENIEC responde 200 pero sin datos utiles
            if (response == null || response.getNumeroDocumento() == null) {
                throw new DniNoEncontradoException(dni);
            }
            return response;

        } catch (RetrofitCallException e) {

            // Caso 1: ni siquiera nos pudimos conectar (no hubo status).
            if (e.esErrorDeRed()) {
                log.error("No se pudo conectar con RENIEC", e);
                throw new ReniecApiException(
                        "No se pudo conectar con el servicio de RENIEC. Intente mas tarde.");
            }

            // Caso 2: hubo respuesta con codigo de error. Decidimos segun el codigo.
            int code = e.getStatusCode();
            if (code == 404) {
                log.warn("RENIEC respondio 404 para el DNI {}", dni);
                throw new DniNoEncontradoException(dni);
            }
            if (code == 401) {
                log.error("Token invalido al consultar RENIEC");
                throw new ReniecApiException("El token de acceso a RENIEC es invalido o expiro.");
            }

            // Cualquier otro codigo de error (400, 500, etc.)
            log.error("RENIEC respondio con error. Status: {}", code);
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

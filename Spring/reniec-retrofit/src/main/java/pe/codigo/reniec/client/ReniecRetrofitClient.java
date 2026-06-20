package pe.codigo.reniec.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.codigo.reniec.dto.PersonaResponse;
import retrofit2.Response;

import java.io.IOException;

/**
 * Este es el cliente que realmente dispara la llamada a RENIEC.
 *
 * Aqui esta LA gran diferencia de Retrofit frente a los otros tres. Pongan
 * mucha atencion:
 *
 *  - execute() es la llamada SINCRONA (bloqueante). Retrofit tambien tiene
 *    enqueue() para asincrono, pero nosotros queremos el flujo de siempre.
 *
 *  - Retrofit NO lanza excepcion cuando RENIEC responde con 404, 401 o 500.
 *    Para Retrofit eso es "una respuesta valida con codigo de error". Por eso
 *    NOSOTROS revisamos response.isSuccessful() y leemos response.code().
 *
 *  - Lo unico que execute() lanza por su cuenta es IOException, y solo cuando
 *    hubo un problema de red (no llego ninguna respuesta).
 *
 * Traducimos ambos casos a nuestra RetrofitCallException para que el service
 * decida el mensaje final.
 */
@Component
@RequiredArgsConstructor
public class ReniecRetrofitClient {

    private final ReniecRetrofitApi reniecRetrofitApi;

    public PersonaResponse consultarDni(String numero) {
        try {
            Response<PersonaResponse> response = reniecRetrofitApi.consultarDni(numero).execute();

            if (response.isSuccessful()) {
                return response.body();
            }

            // Hubo respuesta, pero con codigo de error: lo pasamos con su status.
            throw new RetrofitCallException(
                    response.code(),
                    "RENIEC respondio con codigo " + response.code());

        } catch (IOException e) {
            // No llego respuesta: problema de red (timeout, conexion, DNS...).
            throw new RetrofitCallException(null, "No se pudo conectar con RENIEC");
        }
    }
}

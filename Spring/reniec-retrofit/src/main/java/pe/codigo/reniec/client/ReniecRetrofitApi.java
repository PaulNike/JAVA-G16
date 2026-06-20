package pe.codigo.reniec.client;

import pe.codigo.reniec.dto.PersonaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Esta es la "cara" del API de RENIEC para Retrofit.
 *
 * Se parece muchisimo al cliente de Feign: declaramos una interface con metodos
 * anotados y Retrofit genera la implementacion. Pero ojo con dos detalles:
 *
 *  - Las anotaciones son de Retrofit (retrofit2.http.GET, .Query), NO de Spring.
 *  - El metodo NO devuelve un PersonaResponse directo, sino un Call<PersonaResponse>.
 *    Un Call es como una llamada "preparada pero todavia sin disparar". Quien la
 *    dispara es nuestro ReniecRetrofitClient con .execute().
 *
 * La ruta va SIN "/" al inicio para que se pegue a la baseUrl que termina en "/".
 */
public interface ReniecRetrofitApi {

    @GET("v1/reniec/dni")
    Call<PersonaResponse> consultarDni(@Query("numero") String numero);
}

package pe.codigo.reniec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.codigo.reniec.client.ReniecRetrofitApi;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Aqui construimos Retrofit a mano y dejamos lista nuestra ReniecRetrofitApi
 * para inyectarla en cualquier parte. Vamos paso a paso, que aqui hay varias
 * piezas nuevas:
 */
@Configuration
public class RetrofitConfig {

    @Value("${reniec.api.url}")
    private String baseUrl;

    @Value("${reniec.api.token}")
    private String token;

    @Bean
    public ReniecRetrofitApi reniecRetrofitApi(ObjectMapper objectMapper) {

        // 1) OkHttp es el motor HTTP que usa Retrofit por debajo. Le agregamos
        //    un interceptor que mete el token en CADA peticion. Es la misma idea
        //    del interceptor de Feign y RestTemplate, solo que con la API de OkHttp.
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        // 2) Detalle clasico que hace renegar a todos: Retrofit EXIGE que la
        //    baseUrl termine en "/". Como en el properties la pusimos sin "/",
        //    se lo agregamos aqui si hace falta.
        String base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";

        // 3) Armamos Retrofit: la baseUrl, nuestro OkHttp con el token, y el
        //    convertidor Jackson. Le pasamos el ObjectMapper de Spring para que
        //    use exactamente la misma configuracion de JSON que el resto de la app.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        // 4) Y aqui Retrofit nos genera la implementacion de la interface.
        return retrofit.create(ReniecRetrofitApi.class);
    }
}

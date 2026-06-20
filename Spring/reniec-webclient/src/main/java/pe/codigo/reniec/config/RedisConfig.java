package pe.codigo.reniec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Configuracion de Redis.
 *
 * Por defecto Spring guarda los objetos en formato binario de Java, que es
 * ilegible si abrimos Redis a mano. Aqui armamos un RedisTemplate que guarda:
 *   - las CLAVES como texto plano  (ejemplo: "dni:46027897")
 *   - los VALORES como JSON
 * Asi, si entran con redis-cli y hacen un GET, van a ver los datos legibles.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PersonaResponse> personaRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, PersonaResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // La clave va como String
        template.setKeySerializer(new StringRedisSerializer());

        // El valor va como JSON
        Jackson2JsonRedisSerializer<PersonaResponse> serializer =
                new Jackson2JsonRedisSerializer<>(new ObjectMapper(), PersonaResponse.class);
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}

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
 * Por defecto Spring serializa los objetos en formato binario (JDK),
 * que es ilegible. Aqui creamos un RedisTemplate que:
 *   - guarda las CLAVES como texto plano  (ej: "dni:46027897")
 *   - guarda los VALORES como JSON
 * Asi, si abrimos redis-cli, veremos los datos de forma legible.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PersonaResponse> personaRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, PersonaResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // La clave sera un String
        template.setKeySerializer(new StringRedisSerializer());

        // El valor se guardara como JSON
        Jackson2JsonRedisSerializer<PersonaResponse> serializer =
                new Jackson2JsonRedisSerializer<>(new ObjectMapper(), PersonaResponse.class);
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}

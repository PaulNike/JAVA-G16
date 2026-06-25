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
 * Configuracion de Redis: guardamos las claves como texto y los valores como
 * JSON, para que al abrir redis-cli se vean legibles.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PersonaResponse> personaRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, PersonaResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<PersonaResponse> serializer =
                new Jackson2JsonRedisSerializer<>(new ObjectMapper(), PersonaResponse.class);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}

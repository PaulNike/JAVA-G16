package pe.codigo.reniec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.codigo.reniec.security.JwtAuthenticationFilter;

/**
 * ===========================================================================
 *  SecurityConfig  ·  AUTENTICACION + AUTORIZACION
 * ===========================================================================
 *
 *  QUE CAMBIO RESPECTO AL PROYECTO 2
 *    1. YA NO definimos usuarios en memoria aqui. Ahora salen de la base de
 *       datos a traves de CustomUserDetailsService (que Spring detecta solo por
 *       ser un @Service que implementa UserDetailsService).
 *    2. Agregamos @EnableMethodSecurity para poder usar @PreAuthorize en los
 *       metodos de los controladores (autorizacion "por metodo").
 *    3. Agregamos una regla de autorizacion "por ruta": /api/v1/admin/** solo
 *       para ADMIN.
 *
 *  LAS DOS FORMAS DE AUTORIZAR (las dos aparecen en este proyecto):
 *    - POR RUTA   -> aqui, con requestMatchers(...).hasRole("ADMIN").
 *    - POR METODO -> en el controlador, con @PreAuthorize("hasRole('ADMIN')").
 *
 *  RECORDATORIO 401 vs 403:
 *    - 401 Unauthorized = no sabemos quien eres (fallo la AUTENTICACION).
 *    - 403 Forbidden    = sabemos quien eres, pero no tienes permiso (fallo la
 *                         AUTORIZACION).
 * ===========================================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // habilita @PreAuthorize en los metodos
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                    // Publico: el login (para poder conseguir el token).
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    // AUTORIZACION POR RUTA: la zona de administracion, solo ADMIN.
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    // Todo lo demas: basta con estar autenticado (USER o ADMIN).
                    .anyRequest().authenticated()
            )

            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * El PasswordEncoder sigue siendo BCrypt. Lo usa el login (para comparar) y
     * el sembrador de usuarios CargaInicialUsuarios (para guardar el hash).
     *
     * NOTA: ya NO hay un bean de usuarios aqui. Esos los provee
     * CustomUserDetailsService leyendo de PostgreSQL.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

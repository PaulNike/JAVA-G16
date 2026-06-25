package pe.codigo.reniec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.codigo.reniec.security.JwtAuthenticationFilter;

/**
 * ===========================================================================
 *  SecurityConfig  ·  EL REGLAMENTO DE SEGURIDAD (ahora con JWT)
 * ===========================================================================
 *
 *  QUE CAMBIO RESPECTO AL PROYECTO 1
 *  En el Proyecto 1 usabamos HTTP Basic: mandar usuario y clave en CADA
 *  peticion. Aqui pasamos a JWT, y eso trae cuatro cambios en la cadena:
 *
 *    1. La sesion ahora es STATELESS: el servidor NO guarda sesiones. La unica
 *       "prueba" de quien eres es el token que viaja en cada peticion.
 *    2. Quitamos httpBasic. Ya no se pide usuario/clave en cada llamada.
 *    3. Dejamos PUBLICA la ruta /api/v1/auth/login (sin ella no podrian
 *       loguearse, seria un callejon sin salida).
 *    4. Enganchamos NUESTRO filtro (JwtAuthenticationFilter) ANTES del filtro
 *       estandar, para que valide el token y marque la peticion como autenticada.
 *
 *  Las piezas UserDetailsService y PasswordEncoder siguen igual que en el
 *  Proyecto 1. Lo nuevo es ademas exponer el AuthenticationManager, que el
 *  login usara para validar usuario y clave.
 * ===========================================================================
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * LA CADENA DE FILTROS, adaptada a JWT.
     * Recibe nuestro JwtAuthenticationFilter por inyeccion (es un @Component).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
            // API REST sin estado: CSRF no aplica.
            .csrf(csrf -> csrf.disable())

            // Que se protege y que es publico:
            .authorizeHttpRequests(auth -> auth
                    // El login DEBE ser publico: es la puerta para conseguir el token.
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    // Todo lo demas exige estar autenticado (traer un token valido).
                    .anyRequest().authenticated()
            )

            // STATELESS: no se crean ni usan sesiones HTTP. Cada peticion se
            // valida sola, por su token. Este es el corazon del enfoque con JWT.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Enganchamos nuestro filtro ANTES del de usuario/clave de Spring.
            // Asi, cuando Spring evalua si la peticion esta autenticada, nosotros
            // ya leimos el token y (si era valido) marcamos la identidad.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * EL AuthenticationManager.
     * Es el componente que sabe validar un usuario+clave contra el
     * UserDetailsService y el PasswordEncoder. Lo exponemos como bean para
     * poder inyectarlo en el AuthController y usarlo en el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DE DONDE SALEN LOS USUARIOS (igual que en el Proyecto 1: en memoria).
     * En el Proyecto 3 los moveremos a PostgreSQL.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails alumno = User.builder()
                .username("alumno")
                .password(encoder.encode("codigo123"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(alumno, admin);
    }

    /**
     * COMO SE PROTEGEN LAS CONTRASENAS (BCrypt, igual que en el Proyecto 1).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

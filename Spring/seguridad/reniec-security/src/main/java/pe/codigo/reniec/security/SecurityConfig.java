package pe.codigo.reniec.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================================
 *  EL CENTRO DE TODO EL PROYECTO. Lean esta clase con calma, varias veces.
 * ============================================================================
 *
 * QUE ES SPRING SECURITY (en una idea)
 * ------------------------------------
 * Spring Security se mete EN MEDIO de cada peticion que entra a la aplicacion,
 * ANTES de que llegue a nuestros controllers. Lo hace con una "cadena de filtros"
 * (la famosa filter chain). Piensenlo como los controles de un aeropuerto: antes
 * de subir al avion (el controller) pasan por varios checkpoints: documento,
 * seguridad, puerta de embarque. Si fallan en uno, no avanzan al siguiente.
 *
 * Apenas agregamos la dependencia de Spring Security al pom, TODA la app queda
 * protegida por defecto y Spring genera un usuario "user" con una clave aleatoria
 * que imprime en la consola al arrancar. Nuestro trabajo en esta clase es tomar
 * el control de esa configuracion: decir QUE se abre, QUE se protege y QUIENES
 * son los usuarios validos.
 *
 * AUTENTICACION vs AUTORIZACION (no las confundan)
 * ------------------------------------------------
 *  - Autenticacion = "quien eres?".  Muestras tu usuario y clave (tu DNI en la puerta).
 *  - Autorizacion   = "que puedes hacer?".  Tu rol abre unas puertas y otras no.
 * En ESTE proyecto solo trabajamos la AUTENTICACION: si estas logueado, pasas.
 * La autorizacion por roles la veremos en el Proyecto 3. Una cosa nueva a la vez.
 *
 * LAS TRES PIEZAS QUE DEFINIMOS AQUI
 * ----------------------------------
 *  1. SecurityFilterChain -> las REGLAS: que rutas son publicas y cuales protegidas.
 *  2. UserDetailsService  -> los USUARIOS: de donde salen (aqui, de la memoria).
 *  3. PasswordEncoder     -> como se GUARDAN las claves (cifradas con BCrypt, nunca en texto plano).
 *
 * @Configuration: le dice a Spring que esta clase define beans de configuracion.
 * @EnableWebSecurity: enciende la seguridad web y nos deja personalizarla.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * PIEZA 1: LAS REGLAS DE ACCESO (la filter chain).
     *
     * Este bean es el que decide, para cada peticion, si pasa o no y como debe
     * autenticarse. Lo configuramos con el "Lambda DSL" de Spring Security 6:
     * una forma de escribir las reglas con lambdas, encadenadas y legibles.
     *
     * OJO DE VERSION: en Spring Security 6 (el que trae Spring Boot 3.5) esta es
     * LA UNICA forma de configurar. La clase WebSecurityConfigurerAdapter que veran
     * en tutoriales viejos YA NO EXISTE. Si encuentran "extends WebSecurityConfigurerAdapter",
     * ese tutorial esta desactualizado.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --- CSRF ---
            // CSRF es una proteccion pensada para apps web con formularios y sesiones
            // en el navegador. Nuestra API es stateless y la consumen Postman, curl,
            // apps moviles u otros servicios (no un formulario de navegador), asi que
            // la desactivamos. Si no lo hicieramos, las peticiones POST darian 403.
            .csrf(csrf -> csrf.disable())

            // --- REGLAS DE AUTORIZACION ---
            // Aqui decidimos que se abre y que se protege. MUY IMPORTANTE: las reglas
            // se evaluan de ARRIBA hacia ABAJO y gana la PRIMERA que coincide. Por eso
            // las reglas especificas van primero y la general (anyRequest) va al final.
            .authorizeHttpRequests(auth -> auth
                // Todo lo que cuelgue de /api/v1/public/ queda ABIERTO, sin login.
                .requestMatchers("/api/v1/public/**").permitAll()
                // Cualquier OTRA peticion exige estar autenticado (logueado).
                // Aqui cae nuestro endpoint de personas: /api/v1/personas/**
                .anyRequest().authenticated()
            )

            // --- COMO SE AUTENTICAN ---
            // httpBasic = autenticacion "HTTP Basic": el cliente manda usuario y clave
            // en la cabecera Authorization en CADA peticion. Es la forma mas simple de
            // probar seguridad y la ideal para esta primera clase. En el Proyecto 2 la
            // reemplazaremos por un token JWT para no mandar la clave cada vez.
            .httpBasic(Customizer.withDefaults());

        // Construimos y devolvemos la cadena ya configurada.
        return http.build();
    }

    /**
     * PIEZA 2: LOS USUARIOS (de donde salen).
     *
     * UserDetailsService es la interface que Spring Security usa para BUSCAR un
     * usuario por su nombre cuando alguien intenta loguearse. Aqui usamos su
     * implementacion mas simple, InMemoryUserDetailsManager, que guarda los
     * usuarios EN MEMORIA (en una lista, dentro del programa).
     *
     * Esto es a proposito para esta primera clase: queremos entender Security sin
     * meter todavia una base de datos. En el Proyecto 2 o 3 sacaremos los usuarios
     * de PostgreSQL. Una cosa nueva a la vez.
     *
     * Detalle importante: en cuanto definimos este bean, Spring DEJA de generar
     * aquel usuario "user" con clave aleatoria. A partir de ahora, los usuarios
     * validos son EXACTAMENTE estos dos.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Usuario normal. Fijense que la clave NO se guarda como "alumno123":
        // la pasamos por encoder.encode(...) para guardarla CIFRADA con BCrypt.
        UserDetails alumno = User.withUsername("alumno")
                .password(encoder.encode("alumno123"))
                .roles("USER")   // todo usuario necesita al menos un rol; lo usaremos en el Proyecto 3
                .build();

        // Un segundo usuario con rol ADMIN. Por ahora el rol NO cambia nada
        // (cualquiera logueado entra igual), pero lo dejamos listo para cuando
        // hagamos autorizacion por roles mas adelante.
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        // Los registramos en el "almacen en memoria". Spring buscara aqui cuando
        // alguien intente autenticarse.
        return new InMemoryUserDetailsManager(alumno, admin);
    }

    /**
     * PIEZA 3: EL CIFRADOR DE CLAVES.
     *
     * REGLA DE ORO DE SEGURIDAD: las contrasenas JAMAS se guardan en texto plano.
     * Se guardan "hasheadas", es decir, transformadas en un texto irreversible.
     * BCrypt es el algoritmo estandar para esto. Ademas, BCrypt agrega "sal"
     * (un valor aleatorio), por eso dos personas con la misma clave terminan con
     * hashes distintos.
     *
     * Cuando alguien hace login, Spring toma la clave que escribio, le aplica el
     * MISMO BCrypt y compara el resultado con el hash guardado. Nunca "descifra"
     * la clave (no se puede): solo compara hashes.
     *
     * Este mismo bean lo usa el UserDetailsService de arriba para cifrar las claves
     * al crear los usuarios.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

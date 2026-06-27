package pe.codigo.reniec.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.codigo.reniec.entity.Usuario;
import pe.codigo.reniec.repository.UsuarioRepository;

/**
 * ===========================================================================
 *  CargaInicialUsuarios  ·  SEMBRAR LOS USUARIOS DE PRUEBA
 * ===========================================================================
 *
 *  Como ahora los usuarios viven en la base de datos, necesitamos que existan
 *  ahi. Esta clase los crea automaticamente la primera vez que arranca la app.
 *
 *  COMO FUNCIONA
 *  CommandLineRunner es una interface de Spring Boot: su metodo run(...) se
 *  ejecuta UNA vez, justo despues de que la aplicacion termina de arrancar.
 *  Lo usamos para insertar nuestros dos usuarios SI la tabla esta vacia (asi no
 *  los duplicamos en cada reinicio).
 *
 *  Fijense que la contrasena se guarda con encoder.encode(...): nunca en texto
 *  plano. Y que "admin" tiene DOS roles ("USER,ADMIN") para que pueda hacer
 *  todo lo que hace un USER y, ademas, lo exclusivo de ADMIN.
 * ===========================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CargaInicialUsuarios implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {

            usuarioRepository.save(Usuario.builder()
                    .username("alumno")
                    .password(encoder.encode("codigo123"))
                    .roles("USER")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("admin")
                    .password(encoder.encode("admin123"))
                    .roles("USER,ADMIN")
                    .build());

            log.info("Usuarios iniciales creados: alumno (USER), admin (USER,ADMIN)");
        }
    }
}

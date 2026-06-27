package pe.codigo.reniec.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.codigo.reniec.entity.Usuario;
import pe.codigo.reniec.repository.UsuarioRepository;

import java.util.Arrays;
import java.util.List;

/**
 * ===========================================================================
 *  CustomUserDetailsService  ·  EL PUENTE ENTRE NUESTRA BD Y SPRING SECURITY
 * ===========================================================================
 *
 *  QUE ES
 *  En los proyectos anteriores usabamos un UserDetailsService "en memoria".
 *  Aqui lo reemplazamos por NUESTRA version, que busca los usuarios en
 *  PostgreSQL. Es el unico cambio necesario para que toda la maquinaria de
 *  Spring Security (login, validacion de clave, etc.) use la base de datos.
 *
 *  Como Spring Security detecta automaticamente cualquier bean que implemente
 *  UserDetailsService, con solo marcar esta clase con @Service ya queda en uso.
 *  Por eso en SecurityConfig YA NO definimos usuarios a mano.
 *
 *  QUE HACE loadUserByUsername
 *  1. Busca el usuario en la BD por su username.
 *  2. Si no existe, lanza UsernameNotFoundException (terminara en 401).
 *  3. Si existe, traduce nuestra entidad Usuario al UserDetails que Spring
 *     entiende, convirtiendo el texto de roles ("USER,ADMIN") en autoridades
 *     ("ROLE_USER", "ROLE_ADMIN").
 * ===========================================================================
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Buscamos el usuario en la base de datos.
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Convertimos "USER,ADMIN" en autoridades con el prefijo ROLE_.
        //    OJO: hasRole('ADMIN') de Spring busca la autoridad "ROLE_ADMIN".
        //    Por eso aqui agregamos ese prefijo.
        List<SimpleGrantedAuthority> authorities = Arrays.stream(usuario.getRoles().split(","))
                .map(String::trim)
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                .toList();

        // 3. Devolvemos el UserDetails que Spring Security sabe manejar.
        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
}

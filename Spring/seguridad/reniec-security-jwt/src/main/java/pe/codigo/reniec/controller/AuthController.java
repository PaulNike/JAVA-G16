package pe.codigo.reniec.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.codigo.reniec.dto.LoginRequest;
import pe.codigo.reniec.security.JwtService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ===========================================================================
 *  AuthController  ·  LA PUERTA DE ENTRADA (login) Y EL "QUIEN SOY"
 * ===========================================================================
 *
 *  Tiene dos endpoints:
 *
 *   POST /api/v1/auth/login  (PUBLICO)
 *      Recibe usuario y clave, los valida y, si todo bien, devuelve un TOKEN.
 *      Es el unico lugar donde se usa la contrasena. Despues de esto, el cliente
 *      ya no la vuelve a mandar: lleva el token.
 *
 *   GET /api/v1/auth/me  (PROTEGIDO)
 *      Devuelve quien esta autenticado (segun el token). Util para comprobar
 *      en vivo que el token funciona.
 * ===========================================================================
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * LOGIN. Aqui ocurre la magia del paso "usuario/clave -> token":
     *
     *   1. authenticationManager.authenticate(...) valida usuario y clave
     *      contra el UserDetailsService + BCrypt. Si NO coinciden, lanza una
     *      AuthenticationException (la atrapamos en el GlobalExceptionHandler
     *      y respondemos 401).
     *   2. Si paso, cargamos el usuario y le generamos su token con el JwtService.
     *   3. Devolvemos el token; el cliente lo guardara y lo usara en cada peticion.
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        // 1. Validar usuario y clave. Si falla, lanza excepcion aqui.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usuario(), request.clave())
        );

        // 2. Credenciales correctas: generamos el token.
        UserDetails usuario = userDetailsService.loadUserByUsername(request.usuario());
        String token = jwtService.generarToken(usuario);

        // 3. Respondemos el token.
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("token", token);
        respuesta.put("tipo", "Bearer");
        return respuesta;
    }

    /**
     * GET /api/v1/auth/me  ->  quien esta autenticado (segun el token).
     * El objeto Authentication lo inyecta Spring; lo dejo NUESTRO filtro al
     * validar el token. Si llegamos aqui, es porque el token era valido.
     */
    @GetMapping("/me")
    public Map<String, Object> quienSoy(Authentication authentication) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("usuario", authentication.getName());
        info.put("roles", authentication.getAuthorities());
        info.put("autenticado", authentication.isAuthenticated());
        return info;
    }
}

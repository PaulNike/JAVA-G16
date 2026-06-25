package pe.codigo.reniec.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ===========================================================================
 *  JwtAuthenticationFilter  ·  EL PORTERO QUE REVISA LA PULSERA
 * ===========================================================================
 *
 *  QUE ES
 *  Es NUESTRO propio filtro. Se ejecuta en CADA peticion, antes de llegar al
 *  controlador. Su trabajo: si la peticion trae un token valido, le dice a
 *  Spring "esta persona ya esta autenticada".
 *
 *  POR QUE EXTIENDE OncePerRequestFilter
 *  Porque garantiza que se ejecute UNA sola vez por peticion (sin esa garantia,
 *  en algunos casos un filtro podria correr varias veces). Solo tenemos que
 *  implementar el metodo doFilterInternal.
 *
 *  COMO ENCAJA EN LA CADENA
 *  En SecurityConfig lo enganchamos con .addFilterBefore(...), para que corra
 *  ANTES del filtro estandar de usuario/clave. Asi, cuando Spring llega a
 *  decidir si la peticion esta autenticada, nosotros ya pusimos (o no) la
 *  identidad en el contexto.
 *
 *  EL FLUJO, PASO A PASO (lo de abajo en codigo):
 *    1. Busca la cabecera Authorization: Bearer <token>.
 *    2. Si no hay, deja pasar (si la ruta es protegida, terminara en 401).
 *    3. Si hay, saca el username del token (esto verifica la firma).
 *    4. Carga el usuario y comprueba que el token sea valido y no este vencido.
 *    5. Si todo bien, marca la peticion como autenticada en el SecurityContext.
 * ===========================================================================
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Leemos la cabecera Authorization.
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no viene un token tipo Bearer, no hacemos nada: pasamos al
        //    siguiente filtro. Si la ruta era protegida, Spring respondera 401.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Quitamos el prefijo "Bearer " (7 caracteres) y nos queda el token.
        final String token = authHeader.substring(7);

        String username;
        try {
            // Esto lee el username Y de paso verifica la firma del token.
            username = jwtService.extraerUsername(token);
        } catch (Exception e) {
            // Token corrupto, alterado o vencido -> no autenticamos. Seguimos
            // la cadena; si la ruta es protegida, terminara en 401.
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Si tenemos username y todavia nadie autentico esta peticion...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails usuario = userDetailsService.loadUserByUsername(username);

            // 5. Si el token es valido para ese usuario, lo marcamos como autenticado.
            if (jwtService.esValido(token, usuario)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,                  // el "principal": quien es
                                null,                     // credenciales: ya no hacen falta, el token basta
                                usuario.getAuthorities()  // sus roles/permisos
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Aqui dejamos la identidad guardada para el resto de la peticion.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuamos con la cadena de filtros (y eventualmente el controlador).
        filterChain.doFilter(request, response);
    }
}

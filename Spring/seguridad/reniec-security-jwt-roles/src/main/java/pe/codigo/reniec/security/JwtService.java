package pe.codigo.reniec.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * ===========================================================================
 *  JwtService  ·  LA FABRICA DE TOKENS (ahora con ROLES adentro)
 * ===========================================================================
 *
 *  QUE CAMBIO RESPECTO AL PROYECTO 2
 *  Antes el token solo guardaba QUIEN eres (el username). Ahora ademas guarda
 *  QUE PUEDES HACER: metemos los roles del usuario como un "claim" (un dato
 *  dentro del payload del token).
 *
 *  POR QUE ES UTIL
 *  Como los roles viajan dentro del token, en cada peticion NO necesitamos ir a
 *  la base de datos para saber que permisos tiene la persona: lo leemos del
 *  propio token. Eso es ser "stateless" de verdad.
 *
 *  EL OTRO LADO DE LA MONEDA (para que lo sepan)
 *  Si a un usuario le quitas el rol ADMIN, su token VIEJO seguira diciendo
 *  ADMIN hasta que venza. Por eso los tokens duran poco (aqui, 15 min). En el
 *  mundo real esto se maneja con tokens cortos y "refresh tokens".
 * ===========================================================================
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretBase64;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * CREAR el token. Ademas del username, metemos un claim "roles" con las
     * autoridades del usuario (ej: ["ROLE_USER", "ROLE_ADMIN"]).
     */
    public String generarToken(UserDetails usuario) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expirationMs);

        // Sacamos las autoridades del usuario como una lista de textos.
        List<String> roles = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("roles", roles)        // <-- LO NUEVO: los roles viajan en el token
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getKey())
                .compact();
    }

    /**
     * Leer el username del token. Al parsear, jjwt verifica la firma Y la
     * expiracion: si el token fue alterado o ya vencio, lanza una excepcion.
     */
    public String extraerUsername(String token) {
        return parsearClaims(token).getSubject();
    }

    /**
     * Leer los roles guardados en el token. Devuelve una lista de textos como
     * ["ROLE_USER", "ROLE_ADMIN"].
     */
    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        Object raw = parsearClaims(token).get("roles");
        if (raw instanceof List<?> lista) {
            return lista.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    /**
     * El motor de lectura: verifica el sello (firma) con nuestra clave y
     * devuelve el contenido. Si la firma no calza o el token vencio, falla aqui.
     */
    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

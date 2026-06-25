package pe.codigo.reniec.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * ===========================================================================
 *  JwtService  ·  LA FABRICA DE TOKENS
 * ===========================================================================
 *
 *  QUE ES
 *  Es la clase que sabe DOS cosas sobre los tokens JWT:
 *    - CREARLOS  (cuando alguien hace login correctamente).
 *    - VALIDARLOS (en cada peticion que trae un token).
 *
 *  RECORDATORIO: QUE ES UN JWT
 *  Un JWT (JSON Web Token) tiene tres partes separadas por puntos:
 *        header . payload . signature
 *  El payload lleva datos (quien eres, cuando expira). La signature es el
 *  "sello": se calcula con una clave secreta que solo conoce el servidor.
 *
 *  OJO, MUY IMPORTANTE: el token esta FIRMADO, no ENCRIPTADO. Cualquiera
 *  puede LEER el payload (peguenlo en jwt.io y lo veran), pero NADIE puede
 *  falsificarlo sin la clave secreta. Por eso: jamas pongan una contrasena
 *  ni datos sensibles dentro del token.
 *
 *  ANALOGIA
 *  El token es la pulsera de un concierto: muestras tu DNI una sola vez en la
 *  entrada (el login) y te dan la pulsera. Despues solo enseñas la pulsera.
 *  Tiene un sello especial que no se puede imitar; si la alteras, no entras.
 * ===========================================================================
 */
@Service
public class JwtService {

    // La clave secreta y el tiempo de vida vienen del application.properties.
    @Value("${security.jwt.secret}")
    private String secretBase64;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Convierte la clave de texto (base64) en el objeto SecretKey que jjwt usa
     * para firmar y para verificar. Es la MISMA clave en ambos momentos.
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * CREAR el token. Lo llamamos en el login, cuando el usuario ya demostro
     * quien es. Metemos su nombre como "subject", la fecha de emision y la de
     * expiracion, y firmamos con la clave secreta.
     */
    public String generarToken(UserDetails usuario) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getUsername())  // quien es (queda en el payload)
                .issuedAt(ahora)                 // cuando se emitio
                .expiration(expira)              // cuando vence
                .signWith(getKey())              // el sello: lo firma con la clave secreta
                .compact();                      // lo arma en el string final header.payload.signature
    }

    /**
     * Leer el "subject" (el username) que viaja dentro del token.
     * De paso, al parsear, jjwt VERIFICA la firma: si el token fue alterado o
     * la firma no calza, lanza una excepcion aqui mismo.
     */
    public String extraerUsername(String token) {
        return parsearClaims(token).getSubject();
    }

    /**
     * Decidir si un token es valido para un usuario dado: que el nombre coincida
     * y que NO este vencido.
     */
    public boolean esValido(String token, UserDetails usuario) {
        final String username = extraerUsername(token);
        return username.equals(usuario.getUsername()) && !estaVencido(token);
    }

    private boolean estaVencido(String token) {
        return parsearClaims(token).getExpiration().before(new Date());
    }

    /**
     * El "motor" de lectura: verifica la firma con nuestra clave y devuelve el
     * contenido (claims) del token. Si la firma es invalida o el token esta
     * corrupto, lanza excepcion (la atrapamos en el filtro).
     */
    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())          // verifica el sello con la clave secreta
                .build()
                .parseSignedClaims(token)      // si algo no calza, falla aqui
                .getPayload();
    }
}

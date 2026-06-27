package pe.codigo.reniec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================================
 *  Usuario  ·  AHORA LOS USUARIOS VIVEN EN LA BASE DE DATOS
 * ===========================================================================
 *
 *  En los Proyectos 1 y 2 los usuarios estaban "en memoria", escritos a mano
 *  en SecurityConfig. Eso servia para aprender, pero no es real: si reiniciabas
 *  la app, se perdian, y no podias crear usuarios nuevos sin recompilar.
 *
 *  Aqui los movemos a PostgreSQL. Esta entidad se mapea a la tabla "usuarios".
 *
 *  Sobre el campo "roles": guardamos los roles como un texto separado por comas,
 *  SIN el prefijo ROLE_. Por ejemplo: "USER"  o  "USER,ADMIN". El prefijo ROLE_
 *  se lo agregamos al construir las autoridades, en CustomUserDetailsService.
 *
 *  REGLA QUE SE REPITE: el campo password guarda el HASH (BCrypt), nunca la
 *  contrasena en texto plano.
 * ===========================================================================
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;   // hash BCrypt

    @Column(nullable = false)
    private String roles;      // ej: "USER" o "USER,ADMIN" (sin prefijo ROLE_)
}

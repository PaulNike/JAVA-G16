package pe.codigo.reniec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta es nuestra entidad JPA: se mapea a la tabla "personas" en PostgreSQL.
 *
 * Aqui vamos guardando las consultas que ya resolvimos contra RENIEC, para no
 * tener que volver a llamar (ni pagar) al API externo la proxima vez que nos
 * pregunten por el mismo DNI. Usamos el DNI como llave primaria porque es
 * unico y no cambia.
 */
@Entity
@Table(name = "personas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @Column(name = "numero_documento", length = 8)
    private String numeroDocumento;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "nombre_completo")
    private String nombreCompleto;
}

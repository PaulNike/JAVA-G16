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
 * Entidad JPA: se mapea a la tabla "personas" en PostgreSQL.
 *
 * Aqui guardamos las consultas que YA resolvimos contra RENIEC,
 * para no volver a llamar (ni pagar) al API externo la proxima vez.
 * El DNI es nuestra llave primaria: es unico e inmutable.
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

package pe.codigo.reniec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Este DTO representa la respuesta del API de RENIEC (decolecta).
 *
 * El API nos manda los campos en snake_case (first_name) y nosotros en Java
 * trabajamos en camelCase (nombres). Para que Jackson sepa emparejarlos, le
 * ponemos @JsonProperty: es el puente entre el nombre del JSON y el de Java.
 *
 * Implementamos Serializable porque este mismo objeto lo vamos a guardar en
 * Redis, y Redis necesita poder convertirlo a bytes y de vuelta.
 */
@Data                 // Lombok nos crea getters, setters, equals, hashCode y toString
@Builder              // para construir objetos con .builder()...build()
@NoArgsConstructor    // constructor vacio (lo usa Jackson al deserializar)
@AllArgsConstructor   // constructor con todos los campos
public class PersonaResponse implements Serializable {

    @JsonProperty("first_name")
    private String nombres;

    @JsonProperty("first_last_name")
    private String apellidoPaterno;

    @JsonProperty("second_last_name")
    private String apellidoMaterno;

    @JsonProperty("full_name")
    private String nombreCompleto;

    @JsonProperty("document_number")
    private String numeroDocumento;
}

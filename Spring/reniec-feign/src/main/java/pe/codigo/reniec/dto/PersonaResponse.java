package pe.codigo.reniec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO que representa la respuesta del API externo de RENIEC (decolecta).
 *
 * El API devuelve los campos en snake_case (first_name), pero en
 * Java usamos camelCase (nombres). La anotacion @JsonProperty hace de
 * "puente" entre ambos nombres al momento de leer (deserializar) el JSON.
 *
 * Implementa Serializable porque este objeto tambien se guarda en Redis.
 */
@Data                 // genera getters, setters, equals, hashCode, toString
@Builder              // permite construir objetos con .builder()...build()
@NoArgsConstructor    // constructor vacio (lo necesita Jackson)
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

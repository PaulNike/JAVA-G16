package pe.codigo.reniec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO con la respuesta del API de RENIEC (decolecta).
 *
 * El API manda los campos en snake_case (first_name) y nosotros en Java
 * trabajamos en camelCase (nombres). El @JsonProperty es el puente entre
 * ambos nombres. Implementa Serializable porque tambien se guarda en Redis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

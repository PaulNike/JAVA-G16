package pe.codigo.reniec.dto;

/**
 * Lo que el cliente envia al hacer login: usuario y clave.
 *
 * Es un "record" de Java: una clase inmutable y compacta, perfecta para un DTO
 * que solo transporta datos. Spring la rellena automaticamente desde el JSON
 * del body de la peticion.
 */
public record LoginRequest(String usuario, String clave) {
}

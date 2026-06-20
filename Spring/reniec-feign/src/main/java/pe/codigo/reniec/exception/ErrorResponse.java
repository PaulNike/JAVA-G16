package pe.codigo.reniec.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Estructura uniforme para devolver errores al cliente.
 *
 *  En lugar de devolver un texto suelto, devolvemos siempre el mismo
 * "molde" de error (timestamp, status, error, mensaje). Esto le facilita la
 * vida a quien consume nuestra API (frontend, app movil, etc.).
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
}

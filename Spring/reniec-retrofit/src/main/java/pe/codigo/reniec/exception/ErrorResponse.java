package pe.codigo.reniec.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Este es el "molde" con el que devolvemos los errores al cliente.
 *
 * En lugar de mandar un texto suelto, siempre respondemos con la misma forma
 * (timestamp, status, error, mensaje). Eso le facilita la vida a quien consume
 * nuestra API, sea un frontend, una app movil u otro servicio.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
}

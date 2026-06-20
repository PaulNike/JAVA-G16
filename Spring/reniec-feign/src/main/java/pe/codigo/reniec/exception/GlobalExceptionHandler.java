package pe.codigo.reniec.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejador GLOBAL de excepciones.
 *
 * @RestControllerAdvice intercepta las excepciones que lanzan los
 * controladores y services, y las convierte en respuestas HTTP limpias.
 * Gracias a esto, el usuario NUNCA vera un stacktrace feo: vera un JSON
 * de error con un mensaje personalizado.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 -> el DNI no fue encontrado en ninguna fuente
    @ExceptionHandler(DniNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(DniNoEncontradoException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "DNI no encontrado",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 502 -> el API externo de RENIEC fallo
    @ExceptionHandler(ReniecApiException.class)
    public ResponseEntity<ErrorResponse> handleReniec(ReniecApiException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "Error en servicio RENIEC",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    // 400 -> el DNI no cumple el formato (8 digitos)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ConstraintViolationException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Solicitud invalida",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 500 -> cualquier otro error no contemplado (red de seguridad)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Error inesperado", e);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno",
                "Ocurrio un error inesperado. Contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

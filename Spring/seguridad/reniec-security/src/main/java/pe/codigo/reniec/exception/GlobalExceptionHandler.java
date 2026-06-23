package pe.codigo.reniec.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones de NEGOCIO.
 *
 * Una aclaracion importante de seguridad: los errores 401 (no autenticado) y
 * 403 (no autorizado) NO los maneja esta clase. Esos ocurren ANTES, en la
 * cadena de filtros de Spring Security, antes de llegar al controlador. Este
 * @RestControllerAdvice solo traduce los errores de nuestra logica (404, 502,
 * 400 de validacion, etc.).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DniNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(DniNoEncontradoException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                "DNI no encontrado", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReniecApiException.class)
    public ResponseEntity<ErrorResponse> handleReniec(ReniecApiException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(),
                "Error en servicio RENIEC", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ConstraintViolationException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Solicitud invalida", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Error inesperado", e);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno", "Ocurrio un error inesperado. Contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

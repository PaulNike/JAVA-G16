package pe.codigo.reniec.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones.
 *
 * Aclaracion de seguridad util para entender el flujo:
 *  - El 401 cuando entras a una ruta protegida SIN token lo decide la cadena
 *    de filtros de Spring Security, ANTES de llegar a un controlador (no pasa
 *    por aqui).
 *  - El 401 de un LOGIN fallido (usuario/clave incorrectos) SI pasa por aqui,
 *    porque el authenticationManager lanza la excepcion DENTRO del controlador.
 *  - El resto de handlers traduce errores de nuestra logica (404, 502, 400).
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

    // 401 -> credenciales invalidas en el login (usuario o clave incorrectos).
    // Esta excepcion la lanza el authenticationManager dentro del AuthController.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                "No autenticado", "Usuario o contrasena incorrectos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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

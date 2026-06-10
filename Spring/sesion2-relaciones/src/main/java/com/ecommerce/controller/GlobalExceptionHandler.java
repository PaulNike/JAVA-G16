package com.ecommerce.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sesion 2 - Manejo centralizado de excepciones (Cross-Cutting Concern)
 *
 * @RestControllerAdvice intercepta todas las excepciones no capturadas
 * en los controllers y las convierte en respuestas HTTP estructuradas.
 *
 * Sin esto, Spring devolveria un JSON generico con stacktrace al cliente.
 * Con esto, traducimos excepciones de dominio a codigos HTTP semanticos:
 *
 *   EntityNotFoundException  -> 404 NOT FOUND
 *     "No encontre lo que buscas" - el recurso no existe
 *
 *   IllegalArgumentException -> 400 BAD REQUEST
 *     "Lo que me enviaste es invalido" - error del cliente
 *
 *   IllegalStateException    -> 409 CONFLICT
 *     "Estado de negocio invalido" - ej. stock insuficiente
 *
 * ProblemDetail es el estandar RFC 7807 para respuestas de error en REST APIs.
 * Devuelve: {"type":"...", "status":404, "detail":"mensaje"}.
 *
 * Conexion con SOLID: esto es SRP aplicado a la capa de presentacion.
 * Los Services y Controllers no manejan HTTP; solo lanzan excepciones
 * de dominio que este handler convierte en respuestas apropiadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // IllegalStateException la usa StockValidator -> llega aqui como 409 CONFLICT
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleConflict(IllegalStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}

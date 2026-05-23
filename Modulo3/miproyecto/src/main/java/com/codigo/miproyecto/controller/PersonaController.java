package com.codigo.miproyecto.controller;

import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CONTROLADOR CON VALIDACIÓN DE PARÁMETROS - Bean Validation en capa HTTP
 *
 * Novedad respecto a los controladores anteriores: este controller aplica
 * validaciones directamente sobre los @RequestParam usando anotaciones de
 * Jakarta Bean Validation (antes llamado javax.validation en versiones antiguas).
 *
 * Dependencia necesaria en pom.xml:
 *   <dependency>
 *       <groupId>org.springframework.boot</groupId>
 *       <artifactId>spring-boot-starter-validation</artifactId>
 *   </dependency>
 *
 * @Validated (a nivel de clase):
 *   - Activa la validación de parámetros de método en esta clase.
 *   - SIN esta anotación, las restricciones como @Pattern en los
 *     @RequestParam son ignoradas completamente por Spring.
 *   - Diferencia con @Valid:
 *       @Valid   → se usa sobre objetos (@RequestBody) para validar sus campos internos.
 *       @Validated → se usa sobre la CLASE para validar parámetros simples
 *                    como @RequestParam, @PathVariable, @RequestHeader.
 *   - Internamente, Spring usa AOP (Programación Orientada a Aspectos) para
 *     interceptar las llamadas a los métodos y ejecutar las validaciones
 *     antes de que el método se ejecute.
 *
 * Cuando una validación falla, Spring lanza automáticamente:
 *   ConstraintViolationException → que debe manejarse con @ExceptionHandler
 *   o un @ControllerAdvice global para retornar un error HTTP legible (400 Bad Request).
 */
@RestController
@RequestMapping("/api/v1/personas")
@Validated
public class PersonaController {

    /**
     * ENDPOINT: Buscar persona por DNI
     * HTTP Method : GET
     * URL         : /api/v1/personas/buscar?dni=12345678
     * Respuesta   : 200 OK + String con el DNI buscado
     *               400 Bad Request si el DNI no cumple el patrón
     *
     * @Pattern(regexp = "\\d{8}", message = "...")
     *   Valida que el parámetro 'dni' cumpla con la expresión regular antes
     *   de que el método se ejecute. Si no cumple → ConstraintViolationException.
     *
     *   Desglose de la expresión regular "\\d{8}":
     *     \\d  → en Java el '\' se escapa como '\\', representa [0-9] (cualquier dígito)
     *     {8}  → exactamente 8 repeticiones del patrón anterior
     *     Por tanto: exactamente 8 dígitos numéricos consecutivos.
     *
     *   Ejemplos válidos  : "12345678", "00000001"
     *   Ejemplos inválidos: "1234567" (7 dígitos), "1234567A" (letra), "123456789" (9 dígitos)
     *
     *   message: texto que se incluye en la excepción y puede mostrarse al cliente.
     *
     * @RequestParam String dni:
     *   El parámetro es obligatorio por defecto (required = true).
     *   Si el cliente no lo envía → 400 Bad Request automático de Spring.
     *   Para hacerlo opcional: @RequestParam(required = false) String dni
     */
    @GetMapping("/buscar")
    public String buscarPersonaPorDni(
            @RequestParam
            @Pattern(regexp = "\\d{8}", message = "El Dni debe tener exactamente 8 digitos")
            String dni
    ) {
        return "Buscando persona con DNI: " + dni;
    }

    /**
     * ENDPOINT: Saludar a una persona por nombre
     * HTTP Method : GET
     * URL         : /api/v1/personas/saludar?nombre=Juan
     * Respuesta   : 200 OK + String de saludo
     *               400 Bad Request si el nombre contiene caracteres inválidos
     *
     * @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "...")
     *   Expresión regular más elaborada que acepta letras del alfabeto
     *   español (con tildes y ñ) y espacios. Rechaza números y símbolos.
     *
     *   Desglose de la expresión regular "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$":
     *     ^              → ancla de inicio: el patrón empieza desde el primer carácter
     *     [...]          → clase de caracteres: cualquier carácter dentro del grupo
     *       A-Z          → letras mayúsculas sin tilde (A hasta Z)
     *       a-z          → letras minúsculas sin tilde (a hasta z)
     *       ÁÉÍÓÚáéíóúÑñ → vocales con tilde y letra ñ/Ñ (español)
     *       (espacio)    → permite espacios para nombres compuestos ("Juan Carlos")
     *     +              → una o más repeticiones (el nombre no puede estar vacío)
     *     $              → ancla de fin: el patrón cubre hasta el último carácter
     *
     *   Ejemplos válidos  : "Juan", "María José", "Ñoño"
     *   Ejemplos inválidos: "Juan123" (número), "Ana!" (símbolo), "" (vacío)
     *
     * Importante: ^ y $ aseguran que TODA la cadena cumpla el patrón,
     * no solo una parte de ella. Sin ellos, "Juan123" pasaría la validación
     * porque "Juan" sí cumple el patrón internamente.
     */
    @GetMapping("/saludar")
    public String saludarPersona(
            @RequestParam
            @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo debe contener letras")
            String nombre
    ) {
        return "Holaaaa! " + nombre;
    }
}
package pe.codigo.reniec.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.codigo.reniec.dto.PersonaResponse;
import pe.codigo.reniec.service.ConsultaService;

/**
 * Este es nuestro controlador REST: la puerta de entrada de la API.
 *
 * Fijense que el controlador NO tiene logica de negocio. Solo recibe la
 * peticion, se la pasa al service y devuelve la respuesta. Lo dejamos
 * "delgado" a proposito: cada capa hace lo suyo y nada mas.
 */
@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Validated
public class ConsultaController {

    private final ConsultaService consultaService;

    /**
     * GET /api/v1/personas/{dni}
     *
     * El @Pattern valida que el DNI tenga exactamente 8 digitos ANTES de
     * entrar al service. Si no cumple, Spring corta ahi mismo y responde 400.
     */
    @GetMapping("/{dni}")
    public ResponseEntity<PersonaResponse> consultar(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
            String dni) {

        PersonaResponse response = consultaService.consultarPorDni(dni);
        return ResponseEntity.ok(response);
    }
}

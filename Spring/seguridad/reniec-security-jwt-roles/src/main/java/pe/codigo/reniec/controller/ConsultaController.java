package pe.codigo.reniec.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.codigo.reniec.dto.PersonaResponse;
import pe.codigo.reniec.service.ConsultaService;

/**
 * Controlador de la consulta de personas.
 *
 * Aqui se ve la AUTORIZACION POR METODO con @PreAuthorize. Tenemos dos
 * endpoints con permisos distintos:
 *
 *   GET    /api/v1/personas/{dni}  -> cualquier usuario AUTENTICADO (USER o ADMIN).
 *   DELETE /api/v1/personas/{dni}  -> SOLO ADMIN (lo protege @PreAuthorize).
 *
 * Si un USER intenta el DELETE, recibira 403 Forbidden: sabemos quien es, pero
 * no tiene permiso. Esa es la diferencia clave con el 401 (no autenticado).
 */
@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Validated
public class ConsultaController {

    private final ConsultaService consultaService;

    /** Consultar un DNI: permitido a cualquier usuario autenticado. */
    @GetMapping("/{dni}")
    public ResponseEntity<PersonaResponse> consultar(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
            String dni) {

        PersonaResponse response = consultaService.consultarPorDni(dni);
        return ResponseEntity.ok(response);
    }

    /**
     * Invalidar (borrar de cache y BD) un DNI: accion sensible, SOLO ADMIN.
     *
     * @PreAuthorize se evalua ANTES de entrar al metodo. Si el usuario no tiene
     * el rol ADMIN, Spring responde 403 y este codigo ni siquiera se ejecuta.
     */
    @DeleteMapping("/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> invalidar(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
            String dni) {

        consultaService.invalidar(dni);
        return ResponseEntity.noContent().build();   // 204
    }
}

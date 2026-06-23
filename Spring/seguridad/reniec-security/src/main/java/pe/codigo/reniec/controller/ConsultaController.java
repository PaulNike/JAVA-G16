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
 * Controlador de la consulta de personas. La puerta de entrada de la API.
 *
 * Algo clave para entender la seguridad: este controlador NO sabe nada de
 * autenticacion. No hay ningun "if usuarioLogueado" aqui dentro. Spring
 * Security se ocupa de eso ANTES, en la cadena de filtros. Si la peticion
 * llego hasta este metodo, es porque ya paso el control de seguridad.
 */
@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Validated
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping("/{dni}")
    public ResponseEntity<PersonaResponse> consultar(
            @PathVariable
            @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
            String dni) {

        PersonaResponse response = consultaService.consultarPorDni(dni);
        return ResponseEntity.ok(response);
    }
}

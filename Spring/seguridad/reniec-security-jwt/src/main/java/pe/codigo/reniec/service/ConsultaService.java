package pe.codigo.reniec.service;

import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Contrato del servicio de consulta. Programamos contra la interface para
 * poder cambiar la implementacion (o testear con un mock) sin tocar el
 * controlador. Es la "D" de SOLID: Inversion de Dependencias.
 */
public interface ConsultaService {

    PersonaResponse consultarPorDni(String dni);
}

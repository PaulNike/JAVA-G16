package pe.codigo.reniec.service;

import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Contrato (interface) del servicio de consulta de personas.
 *
 * Programamos contra la INTERFACE, no contra la implementacion.
 * Esto nos permite cambiar la logica interna (o testear con un mock) sin
 * tocar al controlador. Es el principio de Inversion de Dependencias,
 * la "D" de SOLID.
 */
public interface ConsultaService {

    /**
     * Consulta los datos de una persona por su DNI siguiendo el patron de
     * cache en 3 niveles: Redis -> PostgreSQL -> API RENIEC.
     */
    PersonaResponse consultarPorDni(String dni);
}

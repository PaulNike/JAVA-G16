package pe.codigo.reniec.service;

import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Este es el contrato (la interface) de nuestro servicio de consulta.
 *
 * Fijense que programamos contra la interface, no contra la implementacion.
 * Asi, si el dia de manana queremos cambiar como buscamos los datos por
 * dentro, o si queremos probarlo con un mock en un test, el controlador ni se
 * entera. Eso es la "D" de SOLID: Inversion de Dependencias.
 */
public interface ConsultaService {

    /**
     * Consulta los datos de una persona por su DNI, siguiendo el patron de
     * cache en 3 niveles: Redis -> PostgreSQL -> API RENIEC.
     */
    PersonaResponse consultarPorDni(String dni);
}

package pe.codigo.reniec.service;

import pe.codigo.reniec.dto.PersonaResponse;

/**
 * Contrato del servicio de consulta. Programamos contra la interface para
 * poder cambiar la implementacion (o testear con un mock) sin tocar el
 * controlador. Es la "D" de SOLID: Inversion de Dependencias.
 */
public interface ConsultaService {

    PersonaResponse consultarPorDni(String dni);

    /**
     * Borra un DNI del cache (Redis) y de la base de datos. Lo usa el endpoint
     * de administracion (solo ADMIN) para forzar que la proxima consulta vuelva
     * a pedirlo a RENIEC.
     */
    void invalidar(String dni);
}

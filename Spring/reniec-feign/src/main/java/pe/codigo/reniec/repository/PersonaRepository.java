package pe.codigo.reniec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.codigo.reniec.entity.Persona;

/**
 * Repositorio de acceso a datos para la entidad Persona.
 *
 * Al extender JpaRepository<Persona, String>, Spring Data nos
 * regala gratis los metodos findById, save, delete, findAll, etc.
 * El segundo tipo (String) es el tipo de la llave primaria: el DNI.
 * No necesitamos escribir ni una sola consulta a mano.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    // findById(dni) y save(persona) ya vienen incluidos.
}

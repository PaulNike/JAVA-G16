package pe.codigo.reniec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.codigo.reniec.entity.Persona;

/**
 * Repositorio de acceso a datos para la entidad Persona.
 *
 * Solo con extender JpaRepository<Persona, String> ya tenemos gratis los
 * metodos findById, save, delete, findAll, etc. No escribimos ni una consulta
 * a mano. El segundo tipo (String) es el tipo de la llave primaria: el DNI.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    // findById(dni) y save(persona) ya vienen incluidos.
}

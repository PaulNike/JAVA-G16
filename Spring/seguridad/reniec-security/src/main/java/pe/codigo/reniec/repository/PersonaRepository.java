package pe.codigo.reniec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.codigo.reniec.entity.Persona;

/**
 * Repositorio de la entidad Persona.
 *
 * Al extender JpaRepository<Persona, String> tenemos gratis findById, save,
 * etc. El segundo tipo (String) es el de la llave primaria: el DNI.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
}

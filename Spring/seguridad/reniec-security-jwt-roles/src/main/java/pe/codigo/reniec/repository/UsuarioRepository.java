package pe.codigo.reniec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.codigo.reniec.entity.Usuario;

import java.util.Optional;

/**
 * Repositorio de usuarios.
 *
 * Ademas de los metodos gratis de JpaRepository, declaramos findByUsername:
 * Spring Data genera SOLO la consulta a partir del nombre del metodo
 * ("buscar por username"). Lo usaremos para autenticar: cuando alguien hace
 * login, buscamos su usuario por el nombre.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}

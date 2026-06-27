package pe.codigo.reniec.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.codigo.reniec.repository.UsuarioRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ===========================================================================
 *  AdminController  ·  EJEMPLO DE AUTORIZACION POR RUTA (solo ADMIN)
 * ===========================================================================
 *
 *  Este controlador lista los usuarios registrados. Solo deberia poder verlo
 *  un ADMIN.
 *
 *  COMO SE PROTEGE
 *  Fijense que aqui NO hay ninguna anotacion de seguridad. La proteccion la
 *  pusimos por RUTA en SecurityConfig:
 *
 *      .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
 *
 *  Es una de las dos formas de autorizar. La otra (por metodo, con @PreAuthorize)
 *  la mostramos en el ConsultaController, sobre el endpoint de borrado.
 *
 *  Si un usuario con rol USER llama aqui, recibira un 403 Forbidden: SI sabemos
 *  quien es (esta autenticado), pero NO tiene permiso. Ese es el corazon de la
 *  diferencia entre autenticacion (401) y autorizacion (403).
 * ===========================================================================
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/usuarios")
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("roles", u.getRoles());
                    return m;
                })
                .toList();
    }
}

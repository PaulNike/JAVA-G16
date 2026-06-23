package pe.codigo.reniec.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Un controlador "publico" que existe SOLO para que vean la diferencia en vivo.
 *
 * En SecurityConfig dijimos que todo lo que cuelgue de /api/v1/public/ es abierto
 * (permitAll). Este endpoint cae ahi: lo pueden llamar SIN usuario ni clave y
 * responde 200.
 *
 * Comparenlo en clase con /api/v1/personas/{dni}, que NO es publico: ese, sin
 * autenticarse, responde 401. Esa comparacion (uno abierto, otro protegido) es
 * la mejor forma de entender que hace la filter chain.
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "estado", "ok",
                "mensaje", "Este endpoint es publico: no necesita autenticacion"
        );
    }
}

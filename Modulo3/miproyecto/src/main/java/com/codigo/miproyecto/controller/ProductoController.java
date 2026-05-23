package com.codigo.miproyecto.controller;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.model.Producto;
import com.codigo.miproyecto.repository.ProductoRepository;
import com.codigo.miproyecto.service.ProductoServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR REST - Capa de presentación (entrada de peticiones HTTP)
 *
 * @RestController = @Controller + @ResponseBody combinados.
 *   - @Controller: le dice a Spring que esta clase es un componente de tipo controlador
 *     y la registra en el ApplicationContext (contenedor de beans).
 *   - @ResponseBody: indica que el valor retornado por cada método se serializa
 *     directamente al cuerpo de la respuesta HTTP (JSON por defecto con Jackson),
 *     en lugar de buscar una vista (Thymeleaf, JSP, etc.).
 *
 * @RequestMapping("/api/productos"): define la URL base para TODOS los endpoints
 *   de esta clase. Todos los métodos heredan este prefijo.
 *   Ejemplo: GET /api/productos, POST /api/productos, GET /api/productos/5
 *
 * Patrón de diseño aplicado: CONTROLLER del patrón MVC (Model - View - Controller).
 * Responsabilidad única: recibir peticiones, delegar al Service, devolver la respuesta.
 * Nunca debe contener lógica de negocio.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    /**
     * INYECCIÓN DE DEPENDENCIAS por Constructor (forma recomendada en Spring moderno).
     *
     * Spring detecta que el constructor recibe un ProductoServiceImpl y lo inyecta
     * automáticamente desde el contenedor de beans (no necesitas @Autowired si hay
     * un solo constructor).
     *
     * ¿Por qué por constructor y no por campo (@Autowired directo)?
     *   - Permite declarar el campo como 'final' → inmutable y más seguro.
     *   - Facilita los tests unitarios (puedes pasar un mock sin Spring).
     *   - Hace explícitas las dependencias obligatorias de la clase.
     *
     * Buena práctica: depender de la INTERFAZ (ProductoService) en vez de la
     * implementación concreta (ProductoServiceImpl), para respetar el principio
     * de inversión de dependencias (SOLID - D).
     */
    private final ProductoServiceImpl productoService;
    public ProductoController(ProductoServiceImpl productoService) {
        this.productoService = productoService;
    }

    /**
     * ENDPOINT: Listar todos los productos
     * HTTP Method : GET
     * URL         : /api/productos
     * Respuesta   : 200 OK + array JSON de ProductoResponseDTO
     *
     * @GetMapping = atajo de @RequestMapping(method = RequestMethod.GET)
     *
     * DTO (Data Transfer Object): se retorna ProductoResponseDTO en lugar de
     * la entidad Producto directamente. Esto es una buena práctica porque:
     *   - Evita exponer campos internos o sensibles de la entidad (ej: contraseñas).
     *   - Desacopla la API del modelo de base de datos.
     *   - Permite dar forma a la respuesta según lo que el cliente necesita.
     */
    @GetMapping
    public List<ProductoResponseDTO> listarProductos() {
        return productoService.listarProductos();
    }

    /**
     * ENDPOINT: Obtener un producto por su ID (variable de ruta)
     * HTTP Method : GET
     * URL         : /api/productos/{id}   → ejemplo: /api/productos/3
     * Respuesta   : 200 OK + XML del Producto
     *
     * @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
     *   - value = "/{id}": el segmento {id} es una variable de ruta (path variable).
     *   - produces = APPLICATION_XML_VALUE: le indica a Spring que la respuesta
     *     se debe serializar en formato XML (requiere Jackson-dataformat-xml en el pom).
     *     El cliente también debe enviar el header: Accept: application/xml.
     *
     * @PathVariable Long id: extrae el valor {id} de la URL y lo mapea al
     *   parámetro 'id' del método. Spring hace la conversión String → Long automáticamente.
     *
     * Nota: aquí se retorna la entidad Producto directamente (no un DTO).
     * En un proyecto productivo sería preferible usar un DTO también aquí.
     */
    @GetMapping(value = "/{id}")
    public ProductoResponseDTO obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    /**
     * ENDPOINT: Obtener un producto por su ID (parámetro de consulta)
     * HTTP Method : GET
     * URL         : /api/productos/busqueda?id=3
     * Respuesta   : 200 OK + XML del Producto
     *
     * Diferencia clave entre @PathVariable y @RequestParam:
     *
     *   @PathVariable → el valor va DENTRO de la ruta:   /productos/3
     *   @RequestParam → el valor va como query string:    /productos/busqueda?id=3
     *
     * Ambos obtienen el mismo resultado aquí, pero el estilo @PathVariable
     * es más RESTful para identificar recursos, mientras que @RequestParam
     * es más adecuado para filtros, búsquedas o parámetros opcionales.
     *
     * produces = APPLICATION_XML_VALUE: misma lógica que el endpoint anterior,
     * la respuesta será serializada en XML.
     */
    @GetMapping(value = "/busqueda", produces = MediaType.APPLICATION_XML_VALUE)
    public ProductoResponseDTO obtenerProductoPorIdParam(@RequestParam Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    /**
     * ENDPOINT: Crear un nuevo producto
     * HTTP Method : POST
     * URL         : /api/productos
     * Body        : JSON con los datos del nuevo producto (ProductoRequestDTO)
     * Respuesta   : 200 OK + JSON del producto creado (ProductoResponseDTO)
     *
     * @PostMapping = atajo de @RequestMapping(method = RequestMethod.POST)
     *   Se usa POST para operaciones de CREACIÓN de recursos (semántica REST).
     *
     * @RequestBody ProductoRequestDTO producto:
     *   - @RequestBody: le dice a Spring que deserialice el cuerpo JSON de la
     *     petición HTTP y lo convierta en un objeto ProductoRequestDTO (usando Jackson).
     *   - Se usa un DTO de entrada (Request) separado del de salida (Response),
     *     lo que permite controlar exactamente qué campos acepta la API del cliente
     *     y qué campos devuelve, de forma independiente.
     *
     * Mejora sugerida: agregar @ResponseStatus(HttpStatus.CREATED) para retornar
     * el código HTTP 201 en lugar de 200, que es el estándar REST para creación.
     *
     * Mejora sugerida: agregar @Valid antes de @RequestBody y anotaciones de
     * validación en el DTO (ej: @NotNull, @Size) para validar automáticamente
     * la entrada antes de llegar al Service.
     */
    @PostMapping
    public ProductoResponseDTO createProducto(@RequestBody ProductoRequestDTO producto) {
        return productoService.crearProducto(producto);
    }



}
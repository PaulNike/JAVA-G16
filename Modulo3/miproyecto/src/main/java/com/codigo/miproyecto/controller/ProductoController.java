package com.codigo.miproyecto.controller;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.model.Producto;
import com.codigo.miproyecto.repository.ProductoRepository;
import com.codigo.miproyecto.service.ProductoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        //List<ProductoResponseDTO> productos = productoService.listarProductos();
        return ResponseEntity.ok(productoService.listarProductos());  // 200 OK + BODY
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
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
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
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorIdParam(@RequestParam(defaultValue = "1") Long id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
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
    public ResponseEntity<ProductoResponseDTO> createProducto(@RequestBody ProductoRequestDTO producto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoService.crearProducto(producto));
    }

    @DeleteMapping(value = "/del/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    @PutMapping("/update/{id}/prueba")
    public ResponseEntity<ProductoResponseDTO> actualziarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO productoActualziarDTO) {
        ProductoResponseDTO dto = productoService.actualizarProducto(id, productoActualziarDTO);
        return ResponseEntity
                .ok()
                .header("X-PRODUCTO-ID", String.valueOf(dto.getId()))
                .header("X-OPERACION", "ACTUALIZACION")
                .header("X-Timestamp", String.valueOf(System.currentTimeMillis()))
                .body(dto);
        }

        @GetMapping(value = "/busqueda-header", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorIdHedaer(@RequestHeader("X-ID-PRODUCTO") Long id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
    }


    @PostMapping(
            value = "/upload/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> cargarProductosMasivo(
            @RequestParam("archivo")MultipartFile archivo){

        //Validacion de dato
        if(archivo.isEmpty()){
            return ResponseEntity.badRequest().body("Archivo no encontrado");
        }

        //validacion 2
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Archivo debe tener la extesion .csv");
        }

        List<ProductoResponseDTO> producotsCreados = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)
            );
            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null){
                numeroLinea++;

                //Saltamos la primera linea
                if (numeroLinea == 1 && linea.toLowerCase().contains("nombre")){
                    continue;
                }
                //Saltando las lineas vacias
                if (linea.isBlank()) continue;

                //Separar por comas
                String[] columnas = linea.split(",");

                //Validamos que tengamos las 3 columnas
                if (columnas.length != 3)
                {
                    errores.add("Linea "+ numeroLinea + " de columna incorrecta");
                    continue;
                }

                try {
                    ProductoRequestDTO requestDTO = new ProductoRequestDTO();
                    requestDTO.setNombre(columnas[0]);
                    requestDTO.setPrecio(Double.parseDouble(columnas[1]));
                    requestDTO.setStock(Integer.parseInt(columnas[2]));

                    ProductoResponseDTO responseDTO = productoService.crearProducto(requestDTO);
                    producotsCreados.add(responseDTO);

                }catch (NumberFormatException e){
                    //precio o stock púeden ser ivnaldios
                    errores.add("Linea "+ numeroLinea + " : precio o stock púeden son invalidos");
                }

            }

            reader.close();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al leer el archivo" + e.getMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("totalCreado", producotsCreados.size());
        respuesta.put("totalErrores", errores.size());
        respuesta.put("productos", producotsCreados);
        respuesta.put("errores", errores);

        return ResponseEntity.
        status(HttpStatus.CREATED)
                .header("X-TOTAL-CREADOS", String.valueOf(producotsCreados.size()))
                .header("X-TOTAL-ERORES", String.valueOf(errores.size()))
                .body(respuesta);

    }

}
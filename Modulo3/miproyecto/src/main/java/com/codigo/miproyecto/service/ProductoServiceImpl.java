package com.codigo.miproyecto.service;

import com.codigo.miproyecto.dto.ProductoRequestDTO;
import com.codigo.miproyecto.dto.ProductoResponseDTO;
import com.codigo.miproyecto.mapper.ProductoMapper;
import com.codigo.miproyecto.model.Producto;
import com.codigo.miproyecto.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICIO REFACTORIZADO - Versión mejorada con patrón MAPPER
 *
 * Comparación con la versión anterior:
 *
 *   ANTES (sin Mapper):                    AHORA (con Mapper):
 *   ─────────────────────────────────      ──────────────────────────────────
 *   ProductoResponseDTO dto = new...       ProductoMapper.toProductoResponseDto(...)
 *   dto.setId(producto.getId());
 *   dto.setNombre(producto.getNombre());   Una sola línea, limpia y reutilizable
 *   dto.setPrecio(producto.getPrecio());
 *   dto.setStock(producto.getStock());
 *   dto.setMensaje("...");
 *
 * Principio aplicado: DRY (Don't Repeat Yourself).
 * La lógica de mapeo estaba duplicada en cada método. Al extraerla a
 * ProductoMapper, ahora vive en UN solo lugar y se reutiliza desde aquí.
 */
@Service
public class ProductoServiceImpl {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @PostConstruct
    public void init(){
        System.out.println("ProductoServiceImpl Listo para usar");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("PreDestroy : la aplicacion se esta cerrandoy");
    }
    /**
     * Lista todos los productos convirtiéndolos a DTOs de respuesta.
     *
     * Flujo:
     *   Repository → List<Producto> → (Mapper) → List<ProductoResponseDTO>
     *
     * El Mapper encapsula la transformación: el Service ya no necesita
     * saber cómo se construye un ProductoResponseDTO, solo delega al Mapper.
     */
    public List<ProductoResponseDTO> listarProductos() {
        List<Producto> productos = productoRepository.listar();
        List<ProductoResponseDTO> productosDTO = new ArrayList<>();

        for (Producto producto : productos) {
            // Delegamos la transformación Entidad → DTO al Mapper.
            // El mensaje es responsabilidad del Service (lógica de negocio),
            // no del Mapper (que solo transforma estructura).
            ProductoResponseDTO productoDTO = ProductoMapper.toProductoResponseDto(
                    producto, "Producto Listado Correctamente"
            );
            productosDTO.add(productoDTO);
        }

        return productosDTO;

        // Alternativa moderna con Stream API (Java 8+):
        // return productoRepository.listar()
        //     .stream()
        //     .map(p -> ProductoMapper.toProductoResponseDto(p, "Producto Listado Correctamente"))
        //     .collect(Collectors.toList());
    }

    /**
     * Obtiene un producto por ID y lo retorna como DTO.
     *
     * Cambio respecto a la versión anterior:
     *   ANTES → retornaba Producto (entidad cruda, expone el modelo interno)
     *   AHORA → retorna ProductoResponseDTO (consistente con el resto de métodos)
     *
     * Esto es una mejora: la API siempre responde DTOs, nunca entidades directas.
     *
     * Mejora pendiente: si 'productoRepository.buscarPorId(id)' retorna null
     * (producto no existe), ProductoMapper recibirá null y probablemente
     * lanzará NullPointerException. Se debe validar y lanzar una excepción
     * personalizada:
     *
     *   if (producto == null) {
     *       throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
     *   }
     */
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.buscarPorId(id);

        // Una sola línea reemplaza 6 líneas de setters manuales
        return ProductoMapper.toProductoResponseDto(producto, "Producto Obtenido Correctamente");
    }

    /**
     * Crea un nuevo producto usando el Mapper en ambas direcciones.
     *
     * Flujo completo con Mapper:
     *   1. RequestDTO → (Mapper.toProducto) → Entidad sin ID
     *   2. Entidad sin ID → (Repository.guardar) → Entidad con ID
     *   3. Entidad con ID → (Mapper.toProductoResponseDto) → ResponseDTO
     *
     * El Service actúa como director de orquesta:
     *   - Sabe QUÉ pasos ejecutar y en qué orden.
     *   - Delega el CÓMO al Mapper (transformación) y al Repository (persistencia).
     */
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {

        // Paso 1: DTO de entrada → Entidad (Mapper transforma la estructura)
        Producto producto = ProductoMapper.toProducto(requestDTO);
        producto.setFechaCreacion(LocalDate.now());

        // Paso 2: Persistir la entidad (el Repository le asigna el ID)
        Producto productoGuardado = productoRepository.guardar(producto);

        // Paso 3: Entidad guardada (con ID) → DTO de respuesta
        return ProductoMapper.toProductoResponseDto(
                productoGuardado, "Producto Guardado Correctamente"
        );
    }

    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestActualziarDTO) {
        Producto productoExistente = productoRepository.buscarPorId(id);

        if(productoExistente == null)  {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro el producto");
        }

        Producto productoActualizar = ProductoMapper.toProducto(requestActualziarDTO);
        productoActualizar.setId(id);

        Producto productoActualizado = productoRepository.actualizar(productoActualizar);

        return ProductoMapper.toProductoResponseDto(productoActualizado,"Producto Actualizado correctamente");

    }

    public void eliminarProducto(Long id) {
        productoRepository.eliminar(id);
    }
}
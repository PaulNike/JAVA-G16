package com.codigo.miproyecto.repository;

import com.codigo.miproyecto.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORIO - Capa de acceso a datos (Data Access Layer)
 *
 * @Repository: anotación de Spring que cumple dos funciones:
 *   1. Marca esta clase como un componente de tipo repositorio y la
 *      registra en el ApplicationContext (Spring la gestiona como un bean).
 *   2. Activa la traducción automática de excepciones de acceso a datos
 *      (DataAccessException), útil cuando se trabaja con JPA/Hibernate.
 *
 * Responsabilidad única (SOLID - S):
 *   Esta clase SOLO debe saber cómo almacenar y recuperar Productos.
 *   No tiene lógica de negocio ni conoce los DTOs.
 *
 * Persistencia en memoria (para fines didácticos):
 *   En este proyecto los datos viven en una List<Producto> mientras
 *   la aplicación está corriendo. Al reiniciar el servidor, los datos
 *   se pierden. En producción se usaría JPA + una base de datos real
 *   (MySQL, PostgreSQL, H2, etc.) y esta clase extendería JpaRepository.
 *
 * Ejemplo con JPA:
 *   public interface ProductoRepository extends JpaRepository<Producto, Long> {}
 *   Con eso Spring genera automáticamente findAll(), findById(), save(), etc.
 */
@Repository
public class ProductoRepository {

    /**
     * "Base de datos" en memoria: una lista que simula una tabla.
     * 'final' garantiza que la referencia a la lista no cambie,
     * aunque su contenido (los productos) sí puede modificarse.
     */
    private final List<Producto> productos = new ArrayList<>();

    /**
     * Simulación del autoincremento de ID (como en una BD real).
     * Inicia en 4 porque el constructor ya cargó 3 productos (ids 1, 2, 3).
     * En JPA esto lo maneja @GeneratedValue automáticamente.
     */
    private Long contadoId = 4L;

    /**
     * Constructor del repositorio: carga datos iniciales (seed data).
     * Equivalente a un script de inserción inicial en base de datos.
     * Útil para tener datos de prueba disponibles desde el arranque.
     */
    public ProductoRepository() {
        productos.add(new Producto(1L, "LAPTOP",  2500.00, 5));
        productos.add(new Producto(2L, "MOUSE",    100.00, 10));
        productos.add(new Producto(3L, "TECLADO",  150.00, 5));
    }

    /**
     * Retorna todos los productos almacenados.
     * Equivalente SQL: SELECT * FROM productos
     * Equivalente JPA: productoRepository.findAll()
     */
    public List<Producto> listar() {
        return productos;
    }

    /**
     * Busca un producto por su ID iterando la lista.
     * Equivalente SQL: SELECT * FROM productos WHERE id = ?
     * Equivalente JPA: productoRepository.findById(id)
     *
     * Retorna null si no encuentra el producto. Mejor práctica:
     * retornar Optional<Producto> para forzar al llamador a manejar
     * el caso "no encontrado" y evitar NullPointerException.
     */
    public Producto buscarPorId(Long id) {
        for (Producto producto : productos) {
            if (producto.getId().equals(id)) { // .equals() compara valor, no referencia
                return producto;
            }
        }
        return null; // Mejora: return Optional.empty()
    }

    /**
     * Persiste un nuevo producto asignándole un ID autogenerado.
     * Equivalente SQL: INSERT INTO productos VALUES (...)
     * Equivalente JPA: productoRepository.save(producto)
     *
     * El patrón es:
     *   1. Asignar el siguiente ID disponible al objeto.
     *   2. Incrementar el contador para el próximo guardado.
     *   3. Agregar a la lista (simula INSERT).
     *   4. Retornar el objeto ya con su ID asignado.
     */
    public Producto guardar(Producto producto) {
        producto.setId(contadoId);
        contadoId++; // Post-incremento: primero usa el valor, luego incrementa
        productos.add(producto);
        return producto;
    }
}
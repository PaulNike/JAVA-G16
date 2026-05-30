package com.codigo.miproyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO DE ENTRADA (Request) - Patrón DTO (Data Transfer Object)
 *
 * ¿Qué es un DTO?
 *   Un objeto simple (POJO) cuyo único propósito es transportar datos
 *   entre capas o entre el cliente y el servidor. NO tiene lógica de negocio.
 *
 * ¿Por qué usamos un DTO de REQUEST separado de la entidad?
 *   - El cliente no debe poder enviar el campo 'id' al crear un producto
 *     (el id lo asigna el sistema, no el usuario). Al usar este DTO,
 *     controlamos exactamente qué campos acepta la API.
 *   - Desacoplamos la API del modelo interno: si la entidad cambia,
 *     la API puede mantenerse estable y viceversa.
 *   - Seguridad: evitamos el ataque "Mass Assignment" (que el cliente
 *     modifique campos que no debería, como 'activo', 'rol', etc.).
 *
 * Flujo: Cliente → JSON → @RequestBody → ProductoRequestDTO → Service → Entidad
 *
 * Mejora sugerida con Bean Validation (agregar dependencia spring-boot-starter-validation):
 *   @NotBlank(message = "El nombre es obligatorio")
 *   @NotNull @Positive(message = "El precio debe ser positivo")
 *   @Min(0) para stock
 */
public class ProductoRequestDTO {

    // Campos que el cliente PUEDE y DEBE enviar al crear un producto.
    // Nótese la ausencia del campo 'id': el servidor lo asigna, no el cliente.
    @Size(min=1, max=10)
    private String nombre;
    private Double precio;
    @Min(1)
    @Max(9)
    private Integer stock;
    @Email(message = "ESTO NO ES UN CORREO!!!!")
    private String correo;

    /**
     * Constructor vacío (no-args): OBLIGATORIO para que Jackson (librería de
     * serialización/deserialización JSON) pueda instanciar el objeto y luego
     * poblar sus campos con los setters. Sin este constructor, Spring lanzará
     * un error al intentar deserializar el JSON del request body.
     */
    public ProductoRequestDTO() {}

    /**
     * Constructor con todos los campos: útil para crear instancias en tests
     * unitarios o en otras partes del código de forma concisa.
     */
    public ProductoRequestDTO(String nombre, Double precio, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    /**
     * GETTERS Y SETTERS - Encapsulamiento (principio de POO)
     *
     * Los campos son 'private', por lo que el acceso externo se hace
     * únicamente a través de estos métodos públicos.
     *
     * Jackson los necesita para:
     *   - Setters → deserializar: JSON → objeto Java (cuando llega el request)
     *   - Getters → serializar: objeto Java → JSON (cuando se envía la respuesta)
     *
     * Alternativa moderna: usar Lombok (@Getter, @Setter, @Data, @NoArgsConstructor)
     * para eliminar este boilerplate y hacer el código más limpio.
     */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
//ProductoRequestDTO = loque el cliente manda
    // Producto = lo que el sistema usa internamente
    //ProductoResponsetDTO = lo qeu el sistema responde


    //1. Cliente envia sus json
    //2. ProductoRequestDTO
    //3. Service
    //4. Producto
    //5. Respository
    //6. ProductoResponseDTO
    //7. CLiente recibe respuesta (json)

}

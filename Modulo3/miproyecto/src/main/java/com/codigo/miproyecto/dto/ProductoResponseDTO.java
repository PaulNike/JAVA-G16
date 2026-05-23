package com.codigo.miproyecto.dto;

/**
 * DTO DE SALIDA (Response) - Patrón DTO (Data Transfer Object)
 *
 * ¿Por qué un DTO de RESPONSE separado del de REQUEST?
 *   - La respuesta puede contener campos adicionales que el cliente
 *     no envía pero sí necesita recibir: aquí 'id' y 'mensaje'.
 *   - Permite moldear la respuesta según las necesidades del frontend
 *     sin alterar la entidad de dominio.
 *   - Un mismo endpoint podría tener distintas vistas de respuesta
 *     (versiones de API, roles de usuario, etc.).
 *
 * Diferencia con ProductoRequestDTO:
 *   REQUEST  → lo que el cliente ENVÍA    (sin 'id', sin 'mensaje')
 *   RESPONSE → lo que el servidor RETORNA (con 'id' y con 'mensaje')
 *
 * Flujo: Entidad → Service → ProductoResponseDTO → JSON → Cliente
 */
public class ProductoResponseDTO {

    // 'id' generado por el sistema: el cliente lo recibe para saber
    // con qué identificador quedó registrado el recurso.
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;

    // Campo adicional exclusivo de la respuesta: mensaje informativo
    // para el cliente (ej: "Producto creado correctamente").
    // No existe en la entidad ni en el RequestDTO.
    private String mensaje;

    /**
     * Constructor vacío: necesario para que Jackson pueda serializar
     * el objeto a JSON cuando se retorna desde el controlador.
     */
    public ProductoResponseDTO() {}

    /**
     * Constructor completo: permite crear el objeto de respuesta en una
     * sola línea, muy útil dentro del Service al armar la respuesta.
     */
    public ProductoResponseDTO(Long id, String nombre, Double precio,
                               Integer stock, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.mensaje = mensaje;
    }

    // Getters y Setters: misma justificación que en ProductoRequestDTO.
    // Jackson usa los getters para convertir este objeto a JSON en la respuesta.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
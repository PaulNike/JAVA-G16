package com.codigo.miproyecto.model;

import java.time.LocalDate;

/**
 * MODELO / ENTIDAD DE DOMINIO
 *
 * Representa el objeto de negocio central de esta aplicación.
 * Es la clase que modela el concepto real "Producto" en el sistema.
 *
 * ¿Dónde vive en la arquitectura?
 *   Capa de Modelo (Model en MVC). Es el núcleo: el Service la manipula,
 *   el Repository la almacena, y los DTOs la traducen hacia afuera.
 *
 * En proyectos con base de datos real (JPA/Hibernate) esta clase
 * tendría anotaciones adicionales como:
 *   @Entity        → le dice a JPA que esta clase mapea a una tabla en la BD
 *   @Table(name="productos") → nombre de la tabla
 *   @Id            → marca el campo como clave primaria
 *   @GeneratedValue(strategy = GenerationType.IDENTITY) → autoincremento
 *   @Column(name="nombre", nullable=false) → configuración de columna
 *
 * En este proyecto la persistencia es en memoria (List<Producto> en el
 * Repository), por eso no se necesitan esas anotaciones aún.
 *
 * Principio: la entidad NO debe tener lógica de presentación ni depender
 * de frameworks externos. Es un POJO (Plain Old Java Object) puro.
 */
public class Producto {

    private Long id;       // Identificador único del producto
    private String nombre;
    private Double precio;
    private Integer stock; // Cantidad disponible en inventario
    private LocalDate fechaCreacion;

    /**
     * Constructor vacío: requerido por frameworks como Jackson o JPA
     * para instanciar el objeto por reflexión antes de poblar sus campos.
     */
    public Producto() {}

    /**
     * Constructor completo: facilita la creación de objetos Producto
     * en una sola línea (muy usado en el Repository para datos de prueba).
     */
    public Producto(Long id, String nombre, Double precio, Integer stock, LocalDate fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Getters y Setters: proporcionan acceso controlado a los campos privados.
     * Encapsulamiento: el estado interno del objeto solo se modifica
     * a través de estos métodos, nunca accediendo directamente al campo.
     */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
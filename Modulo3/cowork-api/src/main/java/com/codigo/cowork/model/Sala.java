package com.codigo.cowork.model;

/**
 * Entidad interna del dominio. NUNCA se expone directamente en respuestas HTTP:
 * el Controller siempre devuelve un DTO. Esto cumple el principio de no exponer
 * el modelo interno (Tema 2.2).
 */
public class Sala {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer capacidad;
    private String ubicacion;
    private boolean activa;

    public Sala() { }

    public Sala(Long id, String codigo, String nombre, Integer capacidad,
                String ubicacion, boolean activa) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.activa = activa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}

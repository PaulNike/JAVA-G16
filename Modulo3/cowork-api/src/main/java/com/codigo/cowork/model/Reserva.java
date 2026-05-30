package com.codigo.cowork.model;

import java.time.LocalDate;
import java.time.LocalTime;


public class Reserva {

    private Long id;
    private Long salaId;
    private String responsable;
    private String email;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String passwordInterno;

    public Reserva() { }

    public Reserva(Long id, Long salaId, String responsable, String email,
                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                   String estado, String passwordInterno) {
        this.id = id;
        this.salaId = salaId;
        this.responsable = responsable;
        this.email = email;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.passwordInterno = passwordInterno;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPasswordInterno() { return passwordInterno; }
    public void setPasswordInterno(String passwordInterno) { this.passwordInterno = passwordInterno; }
}

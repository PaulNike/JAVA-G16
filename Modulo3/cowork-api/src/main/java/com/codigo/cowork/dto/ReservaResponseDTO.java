package com.codigo.cowork.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;


public record ReservaResponseDTO(
        Long id,
        Long salaId,
        String responsable,
        String email,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fecha,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horaInicio,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horaFin,

        String estado
) { }

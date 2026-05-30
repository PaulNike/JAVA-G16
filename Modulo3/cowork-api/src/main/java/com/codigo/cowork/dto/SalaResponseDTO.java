package com.codigo.cowork.dto;


public record SalaResponseDTO(
        Long id,
        String codigo,
        String nombre,
        Integer capacidad,
        String ubicacion,
        boolean activa,
        String descripcionCorta
) { }

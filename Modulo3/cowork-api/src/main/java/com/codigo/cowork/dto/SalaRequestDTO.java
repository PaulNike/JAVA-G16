package com.codigo.cowork.dto;


public record SalaRequestDTO(
        String codigo,
        String nombre,
        Integer capacidad,
        String ubicacion,
        Boolean activa
) { }

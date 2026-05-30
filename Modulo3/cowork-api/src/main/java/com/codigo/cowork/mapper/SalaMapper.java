package com.codigo.cowork.mapper;

import com.codigo.cowork.dto.SalaRequestDTO;
import com.codigo.cowork.dto.SalaResponseDTO;
import com.codigo.cowork.model.Sala;


public final class SalaMapper {

    private SalaMapper() { }

    public static Sala toEntity(SalaRequestDTO dto) {
        Sala sala = new Sala();
        sala.setCodigo(dto.codigo());
        sala.setNombre(dto.nombre());
        sala.setCapacidad(dto.capacidad());
        sala.setUbicacion(dto.ubicacion());
        sala.setActiva(dto.activa() == null || dto.activa());
        return sala;
    }

    public static SalaResponseDTO toResponse(Sala s) {
        String descripcionCorta = String.format(
                "%s - %s (cap. %d)",
                s.getCodigo(), s.getNombre(), s.getCapacidad()
        );
        return new SalaResponseDTO(
                s.getId(),
                s.getCodigo(),
                s.getNombre(),
                s.getCapacidad(),
                s.getUbicacion(),
                s.isActiva(),
                descripcionCorta
        );
    }

    public static void aplicarCambios(Sala destino, SalaRequestDTO dto) {
        destino.setCodigo(dto.codigo());
        destino.setNombre(dto.nombre());
        destino.setCapacidad(dto.capacidad());
        destino.setUbicacion(dto.ubicacion());
        if (dto.activa() != null) {
            destino.setActiva(dto.activa());
        }
    }
}

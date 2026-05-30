package com.codigo.cowork.mapper;

import com.codigo.cowork.dto.ReservaRequestDTO;
import com.codigo.cowork.dto.ReservaResponseDTO;
import com.codigo.cowork.model.Reserva;


public final class ReservaMapper {

    private ReservaMapper() { }

    public static Reserva toEntity(ReservaRequestDTO dto) {
        Reserva r = new Reserva();
        r.setSalaId(dto.salaId());
        r.setResponsable(dto.responsable());
        r.setEmail(dto.email());
        r.setFecha(dto.fecha());
        r.setHoraInicio(dto.horaInicio());
        r.setHoraFin(dto.horaFin());
        r.setPasswordInterno("token-interno-" + System.nanoTime());
        return r;
    }

    public static ReservaResponseDTO toResponse(Reserva r) {
        return new ReservaResponseDTO(
                r.getId(),
                r.getSalaId(),
                r.getResponsable(),
                r.getEmail(),
                r.getFecha(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getEstado()
        );
    }

    public static void aplicarCambios(Reserva destino, ReservaRequestDTO dto) {
        destino.setSalaId(dto.salaId());
        destino.setResponsable(dto.responsable());
        destino.setEmail(dto.email());
        destino.setFecha(dto.fecha());
        destino.setHoraInicio(dto.horaInicio());
        destino.setHoraFin(dto.horaFin());
    }
}

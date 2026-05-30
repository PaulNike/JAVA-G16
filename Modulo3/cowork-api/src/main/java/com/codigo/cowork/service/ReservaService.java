package com.codigo.cowork.service;

import com.codigo.cowork.dto.ReservaRequestDTO;
import com.codigo.cowork.dto.ReservaResponseDTO;
import com.codigo.cowork.mapper.ReservaMapper;
import com.codigo.cowork.model.Reserva;
import com.codigo.cowork.repository.ReservaRepository;
import com.codigo.cowork.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class ReservaService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("PENDIENTE", "CONFIRMADA", "CANCELADA");

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    public List<ReservaResponseDTO> listar(String estado, LocalDate fecha, Long salaId) {
        return reservaRepository.findByFiltros(estado, fecha, salaId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public Optional<ReservaResponseDTO> buscarPorId(Long id) {
        return reservaRepository.findById(id).map(ReservaMapper::toResponse);
    }

    public List<ReservaResponseDTO> listarPorSala(Long salaId) {
        return reservaRepository.findBySalaId(salaId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }


    public ReservaResponseDTO crear(ReservaRequestDTO dto) {
        if (!salaRepository.existsById(dto.salaId())) {
            throw new RuntimeException("No existe la sala con id " + dto.salaId());
        }
        Reserva nueva = ReservaMapper.toEntity(dto);
        nueva.setEstado("PENDIENTE");
        Reserva guardada = reservaRepository.save(nueva);
        return ReservaMapper.toResponse(guardada);
    }


    public Optional<ReservaResponseDTO> cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new RuntimeException(
                    "Estado invalido: '" + nuevoEstado +
                    "'. Valores permitidos: PENDIENTE, CONFIRMADA, CANCELADA."
            );
        }
        Optional<Reserva> opt = reservaRepository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Reserva r = opt.get();
        r.setEstado(nuevoEstado.toUpperCase());
        reservaRepository.save(r);
        return Optional.of(ReservaMapper.toResponse(r));
    }

    public boolean eliminar(Long id) {
        return reservaRepository.deleteById(id);
    }

    public boolean existe(Long id) {
        return reservaRepository.existsById(id);
    }
}

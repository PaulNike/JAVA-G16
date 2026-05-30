package com.codigo.cowork.service;

import com.codigo.cowork.dto.SalaRequestDTO;
import com.codigo.cowork.dto.SalaResponseDTO;
import com.codigo.cowork.mapper.SalaMapper;
import com.codigo.cowork.model.Sala;
import com.codigo.cowork.repository.ReservaRepository;
import com.codigo.cowork.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository,
                       ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<SalaResponseDTO> listar() {
        return salaRepository.findAll().stream()
                .map(SalaMapper::toResponse)
                .toList();
    }

    public Optional<SalaResponseDTO> buscarPorId(Long id) {
        return salaRepository.findById(id).map(SalaMapper::toResponse);
    }

    public SalaResponseDTO crear(SalaRequestDTO dto) {
        Sala nueva = SalaMapper.toEntity(dto);
        Sala guardada = salaRepository.save(nueva);
        return SalaMapper.toResponse(guardada);
    }

    public Optional<SalaResponseDTO> actualizar(Long id, SalaRequestDTO dto) {
        Optional<Sala> opt = salaRepository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Sala existente = opt.get();
        SalaMapper.aplicarCambios(existente, dto);
        salaRepository.save(existente);
        return Optional.of(SalaMapper.toResponse(existente));
    }


    public boolean eliminar(Long id) {
        if (!salaRepository.existsById(id)) {
            return false;
        }
        reservaRepository.deleteBySalaId(id);
        salaRepository.deleteById(id);
        return true;
    }
}

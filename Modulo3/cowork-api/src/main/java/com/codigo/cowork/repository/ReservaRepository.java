package com.codigo.cowork.repository;

import com.codigo.cowork.model.Reserva;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class ReservaRepository {

    private final List<Reserva> data = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public List<Reserva> findAll() {
        return new ArrayList<>(data);
    }

    public Optional<Reserva> findById(Long id) {
        return data.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public List<Reserva> findBySalaId(Long salaId) {
        return data.stream().filter(r -> r.getSalaId().equals(salaId)).toList();
    }

    public List<Reserva> findByFiltros(String estado, LocalDate fecha, Long salaId) {
        return data.stream()
                .filter(r -> estado == null || estado.equalsIgnoreCase(r.getEstado()))
                .filter(r -> fecha  == null || fecha.equals(r.getFecha()))
                .filter(r -> salaId == null || salaId.equals(r.getSalaId()))
                .toList();
    }

    public Reserva save(Reserva reserva) {
        if (reserva.getId() == null) {
            reserva.setId(secuencia.incrementAndGet());
            data.add(reserva);
        }
        return reserva;
    }

    public boolean deleteById(Long id) {
        return data.removeIf(r -> r.getId().equals(id));
    }

    public int deleteBySalaId(Long salaId) {
        List<Reserva> aEliminar = data.stream()
                .filter(r -> r.getSalaId().equals(salaId))
                .toList();
        data.removeAll(aEliminar);
        return aEliminar.size();
    }

    public boolean existsById(Long id) {
        return data.stream().anyMatch(r -> r.getId().equals(id));
    }
}

package com.codigo.cowork.repository;

import com.codigo.cowork.model.Sala;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class SalaRepository {

    private final List<Sala> data = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @PostConstruct
    public void inicializar() {
        save(new Sala(null, "SALA-A1", "Sala Andes",    12, "Piso 3", true));
        save(new Sala(null, "SALA-B2", "Sala Pacifico",  8, "Piso 5", true));
        save(new Sala(null, "SALA-C3", "Sala Amazonas", 20, "Piso 7", true));
    }

    public List<Sala> findAll() {
        return new ArrayList<>(data);
    }

    public Optional<Sala> findById(Long id) {
        return data.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public Sala save(Sala sala) {
        if (sala.getId() == null) {
            sala.setId(secuencia.incrementAndGet());
            data.add(sala);
        }
        return sala;
    }

    public boolean deleteById(Long id) {
        return data.removeIf(s -> s.getId().equals(id));
    }

    public boolean existsById(Long id) {
        return data.stream().anyMatch(s -> s.getId().equals(id));
    }
}

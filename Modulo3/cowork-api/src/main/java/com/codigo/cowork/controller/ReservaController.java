package com.codigo.cowork.controller;

import com.codigo.cowork.dto.ComprobanteResponseDTO;
import com.codigo.cowork.dto.ReservaRequestDTO;
import com.codigo.cowork.dto.ReservaResponseDTO;
import com.codigo.cowork.service.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }


    @GetMapping
    public List<ReservaResponseDTO> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long salaId
    ) {
        return reservaService.listar(estado, fecha, salaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscar(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sala/{salaId}")
    public List<ReservaResponseDTO> listarPorSala(@PathVariable Long salaId) {
        return reservaService.listarPorSala(salaId);
    }


    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO creada = reservaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ReservaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado
    ) {
        return reservaService.cambiarEstado(id, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminada = reservaService.eliminar(id);
        return eliminada
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


    @PostMapping(
            value = "/{id}/comprobante",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ComprobanteResponseDTO> subirComprobante(
            @PathVariable Long id,
            @RequestHeader("X-Cliente-Id") String clienteId,
            @RequestParam("file") MultipartFile file
    ) {
        if (!reservaService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ComprobanteResponseDTO respuesta = new ComprobanteResponseDTO(
                id,
                clienteId,
                file.getOriginalFilename(),
                file.getSize(),
                "Comprobante recibido correctamente"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}

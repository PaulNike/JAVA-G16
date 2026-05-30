package com.codigo.cowork.controller;

import com.codigo.cowork.dto.InfoResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class InfoController {

    @GetMapping("/info")
    public InfoResponseDTO info() {
        return new InfoResponseDTO(
                "CoworkLima API",
                "1.0.0",
                "Equipo Backend CodiGo"
        );
    }
}

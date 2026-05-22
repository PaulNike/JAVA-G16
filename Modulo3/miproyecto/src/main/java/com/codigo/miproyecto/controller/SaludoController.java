package com.codigo.miproyecto.controller;

import com.codigo.miproyecto.service.SaludoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/saludos")
public class SaludoController {


    @Autowired
    private SaludoServiceImpl saludoServiceImpl;

    @GetMapping("/hola")
    String saludo(){
        return "Hola Spring Boot";
    }

    @GetMapping("/hola-impl")
    String saludoService(){
        return saludoServiceImpl.getSaludo();
    }




}

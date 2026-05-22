package com.codigo.miproyecto.service;

import org.springframework.stereotype.Service;

@Service
public class SaludoServiceImpl {

    public String getSaludo(){
        return "Hola Spring Boot, desde la service";
    }
}

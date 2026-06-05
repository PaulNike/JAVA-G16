package com.codigo.controller;

import com.codigo.entity.Customer;
import com.codigo.repository.CustomerRepository;
import com.codigo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        return new ResponseEntity<>(service.create(customer), HttpStatus.CREATED);
    }

    @GetMapping("/find-email")
    public ResponseEntity<Customer> findByEmail(@RequestParam String email) {
        return new ResponseEntity<>(service.findByEmail(email), HttpStatus.OK);
    }
    @GetMapping("/find-contain")
    public ResponseEntity<List<Customer>> buscarPorLike(@RequestParam String name) {
        return new ResponseEntity<>(service.buscarPorLike(name), HttpStatus.OK);
    }

    @GetMapping("/query-nativo")
    public ResponseEntity<List<Customer>> buscarPorQueryNativo(@RequestParam String name) {
        return new ResponseEntity<>(service.buscarPorNombreSqlNativo(name), HttpStatus.OK);
    }
    @GetMapping("/jpql")
    public ResponseEntity<List<Customer>> buscarPorJpql(@RequestParam String name) {
        return new ResponseEntity<>(service.buscarPorNombreJpql(name), HttpStatus.OK);
    }
    @GetMapping("/consulta-lenta")
    public ResponseEntity<List<Customer>> consultaLenta(@RequestParam String name) {
        return new ResponseEntity<>(service.consultaSlow(name), HttpStatus.OK);
    }
}

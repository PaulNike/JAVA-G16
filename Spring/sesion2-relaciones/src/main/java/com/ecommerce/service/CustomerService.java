package com.ecommerce.service;

import com.ecommerce.entity.Customer;
import com.ecommerce.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;


    @Transactional(readOnly = true)
    public List<Customer> getAll() {
        log.info("Buscando todos los clientes");
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
    }


    @Transactional
    public Customer create(Customer customer) {
        log.info("Creando cliente: {}", customer.getEmail());

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + customer.getEmail());
        }

        Customer saved = customerRepository.save(customer);
        log.info("Cliente creado con id: {}", saved.getId());
        return saved;
    }


    @Transactional
    public Customer update(Long id, Customer datosNuevos) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));

        // Si cambia el email, validar que no lo use otro cliente
        if (!customer.getEmail().equals(datosNuevos.getEmail())
                && customerRepository.findByEmail(datosNuevos.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + datosNuevos.getEmail());
        }

        customer.setName(datosNuevos.getName());
        customer.setEmail(datosNuevos.getEmail());
        customer.setAddress(datosNuevos.getAddress());

        log.info("Cliente {} actualizado (dirty checking activo)", id);
        return customer;
    }


    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente no encontrado con id: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Cliente {} eliminado", id);
    }
}

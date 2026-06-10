package com.ecommerce.service;

import com.ecommerce.entity.Customer;
import com.ecommerce.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE: CustomerService
 * =========================
 * Capa de negocio para la gestión de clientes (CRUD).
 *
 * Sigue las mismas convenciones que ProductService:
 *   - @Transactional(readOnly = true) en lecturas
 *   - @Transactional en escrituras
 *   - Dirty checking en update (no se llama save() al final)
 *   - Excepciones traducidas a HTTP por GlobalExceptionHandler
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    // ----------------------------------------------------------------
    // LECTURA
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------
    // ESCRITURA
    // ----------------------------------------------------------------

    /**
     * CREAR CLIENTE
     * El email es UNIQUE en la BD: validamos antes para dar un mensaje claro
     * (400 Bad Request) en lugar de un error de constraint de la base de datos.
     */
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

    /**
     * ACTUALIZAR CLIENTE (dirty checking)
     * La entidad recuperada está MANAGED dentro de la transacción,
     * por lo que Hibernate genera el UPDATE automáticamente al hacer commit.
     */
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

    /**
     * ELIMINAR CLIENTE
     * Nota: si el cliente tiene pedidos asociados, la BD puede rechazar el
     * borrado por la foreign key (customer_id en orders).
     */
    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente no encontrado con id: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Cliente {} eliminado", id);
    }
}

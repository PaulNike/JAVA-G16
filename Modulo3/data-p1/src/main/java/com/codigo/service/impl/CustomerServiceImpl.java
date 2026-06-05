package com.codigo.service.impl;

import com.codigo.entity.Customer;
import com.codigo.repository.CustomerRepository;
import com.codigo.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    @Transactional
    @Override
    public Customer create(Customer customer) {
        log.info("Creando el cliente: {}", customer.getName());

        //Customer newCustomer = repo.save(customer);
        return repo.save(customer);
    }

    @Override
    public Customer findByEmail(String email) {
        if (repo.findByEmail(email).isPresent()) {
            return repo.findByEmail(email).get();
        }else {
            return null;
        }
    }

    @Override
    public List<Customer> buscarPorLike(String name) {
        return repo.findByNameContaining(name);
    }

    @Override
    public List<Customer> buscarPorNombreSqlNativo(String name) {
        return repo.buscarPorNombreQueryNativo(name);
    }

    @Override
    public List<Customer> buscarPorNombreJpql(String name) {
        return repo.buscarPorNombreJpql(name);
    }

    @Override
    public List<Customer> consultaSlow(String name) {
        return repo.consultaLente(name);
    }
}

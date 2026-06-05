package com.codigo.service;

import com.codigo.entity.Customer;


import java.util.List;

public interface CustomerService {

    Customer create(Customer customer);
    Customer findByEmail(String email);
    List<Customer> buscarPorLike(String name);

    List<Customer> buscarPorNombreSqlNativo(String name);
    List<Customer> buscarPorNombreJpql(String name);
    List<Customer> consultaSlow(String name);
}

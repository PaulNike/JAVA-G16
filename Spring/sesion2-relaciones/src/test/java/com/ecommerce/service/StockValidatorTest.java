package com.ecommerce.service;

import com.ecommerce.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class StockValidatorTest {

    private StockValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StockValidator();
    }


}
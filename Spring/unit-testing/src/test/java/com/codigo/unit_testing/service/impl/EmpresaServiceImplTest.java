package com.codigo.unit_testing.service.impl;

import com.codigo.unit_testing.aggregates.constants.Constants;
import com.codigo.unit_testing.aggregates.request.EmpresaRequest;
import com.codigo.unit_testing.aggregates.response.BaseResponse;
import com.codigo.unit_testing.dao.EmpresaRepository;
import com.codigo.unit_testing.entity.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EmpresaServiceImplTest {

    //primera cosa que hace mockito
    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaServiceImpl empresaServiceImpl;

    private EmpresaRequest empresaRequest;
    private Empresa empresa;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        empresa = new Empresa();
        empresaRequest = new EmpresaRequest();
        empresaRequest.setNumeroDocumento("ssssssssss");
    }

    @Test
    void testCrearEmpresaHappyPath(){
        //arrange
        when(empresaRepository.existsByNumeroDocumento(anyString())).thenReturn(false);
        when(empresaRepository.save(any())).thenReturn(empresa);

        //act
        ResponseEntity<BaseResponse<Empresa>> resultado  = empresaServiceImpl.crear(empresaRequest);
        //assert
        assertEquals(Constants.CODE_OK, resultado.getBody().getCode());
        assertTrue(resultado.getBody().getObjeto().isPresent());
    }
}
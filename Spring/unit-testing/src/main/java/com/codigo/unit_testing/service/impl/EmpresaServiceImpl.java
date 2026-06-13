package com.codigo.unit_testing.service.impl;


import com.codigo.unit_testing.aggregates.constants.Constants;
import com.codigo.unit_testing.aggregates.request.EmpresaRequest;
import com.codigo.unit_testing.aggregates.response.BaseResponse;
import com.codigo.unit_testing.dao.EmpresaRepository;
import com.codigo.unit_testing.entity.Empresa;
import com.codigo.unit_testing.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Override
    public ResponseEntity<BaseResponse<Empresa>> crear(EmpresaRequest request) {
        boolean exists = empresaRepository.existsByNumeroDocumento(request.getNumeroDocumento());

        if (exists) {
            return buildResponse(
                    Constants.CODE_EXIST,
                    Constants.MSJ_EXIST,
                    Optional.empty(), HttpStatus.CONFLICT);
        }

        Empresa empresa = empresaRepository.save(toEntity(request));
        return buildResponse(
                Constants.CODE_OK, //"OK"
                Constants.MSJ_OK, // "TRANSACCION EXITOSA"
                Optional.of(empresa), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BaseResponse<Empresa>> obtenerEmpresa(Long id) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        if (empresaOpt.isPresent()) {
            return buildResponse(Constants.CODE_OK, Constants.MSJ_OK, empresaOpt, HttpStatus.OK);
        }

        return buildResponse(Constants.CODE_EMPRESA_NO_EXIST, Constants.MSJ_EMPRESA_NO_EXIST, Optional.empty(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse<List<Empresa>>> obtenerTodos() {
        List<Empresa> empresas = empresaRepository.findAll();

        if (!empresas.isEmpty()) {
            return buildResponseList(Constants.CODE_OK, Constants.MSJ_OK, Optional.of(empresas));
        }

        return buildResponseList(Constants.CODE_EMPRESA_NO_EXIST, Constants.MSJ_EMPRESA_NO_EXIST, Optional.empty());
    }

    @Override
    public ResponseEntity<BaseResponse<Empresa>> actualizar(Long id, EmpresaRequest empresaRequest) {
        if (!empresaRepository.existsById(id)) {
            return buildResponse(Constants.CODE_EMPRESA_NO_EXIST, Constants.MSJ_EMPRESA_NO_EXIST, Optional.empty(), HttpStatus.OK);
        }

        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Empresa empresaActualizada = updateEntity(empresaRequest, empresaExistente);
        Empresa empresaGuardada = empresaRepository.save(empresaActualizada);

        return buildResponse(Constants.CODE_OK, Constants.MSJ_OK, Optional.of(empresaGuardada), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse<Empresa>> delete(Long id) {
        if (!empresaRepository.existsById(id)) {
            return buildResponse(Constants.CODE_EMPRESA_NO_EXIST, Constants.MSJ_EMPRESA_NO_EXIST, Optional.empty(), HttpStatus.OK);
        }

        Empresa empresa = empresaRepository.findById(id).orElseThrow();
        empresa.setEstado(0);
        empresa.setUsuaDelet(Constants.AUDIT_ADMIN);
        empresa.setDateDelet(currentTimestamp());

        Empresa eliminada = empresaRepository.save(empresa);
        return buildResponse(Constants.CODE_OK, Constants.MSJ_OK, Optional.of(eliminada), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseResponse<Empresa>> obtenerEmpresaXNumDoc(String numDocu) {
        return null;
    }

    // ====================== MÉTODOS PRIVADOS ======================

    private Empresa toEntity(EmpresaRequest request) {
        Empresa entity = new Empresa();
        entity.setRazonSocial(request.getRazonSocial());
        entity.setTipoDocumento(request.getTipoDocumento());
        entity.setNumeroDocumento(request.getNumeroDocumento());
        entity.setCondicion(Constants.CONDICION);
        entity.setDireccion(request.getDireccion());
        entity.setDistrito(request.getDistrito());
        entity.setProvincia(request.getProvincia());
        entity.setDepartamento(request.getDepartamento());
        entity.setEstado(Constants.STATUS_ACTIVE);
        entity.setEsAgenteRetencion(Constants.AGENTE_RETENCION_TRUE);
        entity.setUsuaCrea(Constants.AUDIT_ADMIN);
        entity.setDateCreate(currentTimestamp());
        return entity;
    }

    private Empresa updateEntity(EmpresaRequest request, Empresa empresa) {
        empresa.setRazonSocial(request.getRazonSocial());
        empresa.setTipoDocumento(request.getTipoDocumento());
        empresa.setNumeroDocumento(request.getNumeroDocumento());
        empresa.setDireccion(request.getDireccion());
        empresa.setDistrito(request.getDistrito());
        empresa.setProvincia(request.getProvincia());
        empresa.setDepartamento(request.getDepartamento());
        empresa.setUsuaModif(Constants.AUDIT_ADMIN);
        empresa.setDateModif(currentTimestamp());
        return empresa;
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    private <T> ResponseEntity<BaseResponse<T>> buildResponse(int code, String message, Optional<T> data, HttpStatus status) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return new ResponseEntity<>(response, status);
    }

    private ResponseEntity<BaseResponse<List<Empresa>>> buildResponseList(int code, String message, Optional<List<Empresa>> data) {
        BaseResponse<List<Empresa>> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<BaseResponse<List<Empresa>>> buildResponseList1(int code, String message, Optional<List<Empresa>> data) {
        BaseResponse<List<Empresa>> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<BaseResponse<List<Empresa>>> buildResponseList2(int code, String message, Optional<List<Empresa>> data) {
        BaseResponse<List<Empresa>> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<BaseResponse<List<Empresa>>> buildResponseList3(int code, String message, Optional<List<Empresa>> data) {
        BaseResponse<List<Empresa>> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<BaseResponse<List<Empresa>>> buildResponseList4(int code, String message, Optional<List<Empresa>> data) {
        BaseResponse<List<Empresa>> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setObjeto(data);
        return ResponseEntity.ok(response);
    }
}

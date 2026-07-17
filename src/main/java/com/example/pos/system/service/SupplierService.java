package com.example.pos.system.service;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO supplierDTO) throws Exception;

    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) throws Exception;

    void deleteSupplier(Long id) throws Exception;

    List<SupplierDTO> getAllSuppliers() throws Exception;

    SupplierDTO getSupplierById(Long id) throws Exception;

    // Multi Tenant

    List<SupplierDTO> getAllSuppliers(User user) throws Exception;

    SupplierDTO getSupplierById(Long id, User user) throws Exception;
}
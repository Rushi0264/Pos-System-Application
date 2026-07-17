package com.example.pos.system.mapper;

import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.Supplier;
import com.example.pos.system.payload.dto.SupplierDTO;

public class SupplierMapper {

    public static SupplierDTO toDTO(Supplier supplier) {

        return SupplierDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .gstNumber(supplier.getGstNumber())
                .address(supplier.getAddress())
                .storeId(supplier.getStore().getId())
                .store(StoreMapper.toDTO(supplier.getStore()))
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    public static Supplier toEntity(SupplierDTO dto, Store store) {

        return Supplier.builder()
                .name(dto.getName())
                .contactPerson(dto.getContactPerson())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .gstNumber(dto.getGstNumber())
                .address(dto.getAddress())
                .store(store)
                .build();
    }
}
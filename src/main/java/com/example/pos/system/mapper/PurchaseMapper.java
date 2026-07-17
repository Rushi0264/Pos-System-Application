package com.example.pos.system.mapper;

import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Purchase;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.Supplier;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.PurchaseDTO;

import java.util.stream.Collectors;

public class PurchaseMapper {

    public static PurchaseDTO toDTO(Purchase purchase) {

        return PurchaseDTO.builder()
                .id(purchase.getId())
                .supplierId(
                        purchase.getSupplier() != null ?
                                purchase.getSupplier().getId() : null
                )
                .supplier(
                        purchase.getSupplier() != null ?
                                SupplierMapper.toDTO(purchase.getSupplier()) : null
                )
                .storeId(
                        purchase.getStore() != null ?
                                purchase.getStore().getId() : null
                )
                .branchId(
                        purchase.getBranch() != null ?
                                purchase.getBranch().getId() : null
                )
                .createdById(
                        purchase.getCreatedBy() != null ?
                                purchase.getCreatedBy().getId() : null
                )
                .totalAmount(purchase.getTotalAmount())
                .paymentType(purchase.getPaymentType())
                .invoiceNumber(purchase.getInvoiceNumber())
                .remarks(purchase.getRemarks())
                .items(
                        purchase.getItems() == null ? null :
                                purchase.getItems()
                                        .stream()
                                        .map(PurchaseItemMapper::toDTO)
                                        .collect(Collectors.toList())
                )
                .createdAt(purchase.getCreatedAt())
                .updatedAt(purchase.getUpdatedAt())
                .build();
    }

    public static Purchase toEntity(
            PurchaseDTO dto,
            Supplier supplier,
            Store store,
            Branch branch,
            User createdBy
    ) {

        return Purchase.builder()
                .supplier(supplier)
                .store(store)
                .branch(branch)
                .createdBy(createdBy)
                .totalAmount(dto.getTotalAmount())
                .paymentType(dto.getPaymentType())
                .invoiceNumber(dto.getInvoiceNumber())
                .remarks(dto.getRemarks())
                .build();
    }

}
package com.example.pos.system.service;

import com.example.pos.system.payload.dto.PurchaseDTO;

import java.util.List;

public interface PurchaseService {

    PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) throws Exception;

    PurchaseDTO updatePurchase(Long id, PurchaseDTO purchaseDTO) throws Exception;

    void deletePurchase(Long id) throws Exception;

    PurchaseDTO getPurchaseById(Long id) throws Exception;

    List<PurchaseDTO> getAllPurchases() throws Exception;

    List<PurchaseDTO> getPurchasesBySupplier(Long supplierId) throws Exception;

}
package com.example.pos.system.repository;

import com.example.pos.system.modal.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByStoreId(Long storeId);

    List<Purchase> findByBranchId(Long branchId);

    List<Purchase> findBySupplierId(Long supplierId);

    boolean existsByInvoiceNumber(String invoiceNumber);

}
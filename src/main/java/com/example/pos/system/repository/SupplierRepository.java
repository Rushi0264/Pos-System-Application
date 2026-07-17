package com.example.pos.system.repository;

import com.example.pos.system.modal.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByStoreId(Long storeId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
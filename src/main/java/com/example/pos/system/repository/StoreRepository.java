package com.example.pos.system.repository;

import com.example.pos.system.modal.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByStoreAdminId(Long adminId);
}

package com.example.pos.system.repository;

import com.example.pos.system.modal.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pos.system.domain.StoreStatus;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    long countByStatus(StoreStatus status);
    List<Store> findTop5ByOrderByCreatedAtDesc();
}
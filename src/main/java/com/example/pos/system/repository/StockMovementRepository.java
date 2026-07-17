package com.example.pos.system.repository;


import com.example.pos.system.modal.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StockMovementRepository
        extends JpaRepository<StockMovement,Long> {


    List<StockMovement> findByBranchId(Long branchId);


    List<StockMovement> findByProductId(Long productId);


}
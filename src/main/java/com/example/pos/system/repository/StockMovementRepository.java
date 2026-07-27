package com.example.pos.system.repository;


import com.example.pos.system.modal.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StockMovementRepository
        extends JpaRepository<StockMovement,Long> {


    List<StockMovement> findByBranchId(Long branchId);


    List<StockMovement> findByProductId(Long productId);

    @Query("SELECT COALESCE(SUM(CASE " +
            "WHEN sm.type = 'PURCHASE' THEN sm.quantity " +
            "WHEN sm.type = 'TRANSFER_IN' THEN sm.quantity " +
            "WHEN sm.type = 'TRANSFER_OUT' THEN -sm.quantity " +
            "ELSE 0 END), 0) " +
            "FROM StockMovement sm " +
            "WHERE sm.store.id = :storeId AND sm.product.id = :productId AND sm.branch IS NULL")
    Integer getStoreStock(@Param("storeId") Long storeId, @Param("productId") Long productId);

}
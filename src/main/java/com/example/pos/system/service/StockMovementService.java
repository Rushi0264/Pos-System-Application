package com.example.pos.system.service;


import com.example.pos.system.payload.dto.StockMovementDTO;

import java.util.List;


public interface StockMovementService {


    StockMovementDTO createMovement(
            StockMovementDTO dto
    ) throws Exception;



    List<StockMovementDTO> getByBranch(
            Long branchId
    );

    List<StockMovementDTO> getByProduct(Long productId);

    void transferStockToBranch(Long storeId, Long branchId, Long productId, Integer quantity) throws Exception;

    Integer getStoreStock(Long storeId, Long productId) throws Exception;

}
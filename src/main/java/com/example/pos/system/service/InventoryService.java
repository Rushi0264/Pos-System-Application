package com.example.pos.system.service;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {

    InventoryDTO createInventory(InventoryDTO inventoryDTO) throws Exception;

    InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO) throws Exception;

    void deleteInventory(Long id) throws Exception;

    List<InventoryDTO> getAllInventory() throws Exception;

    InventoryDTO getInventoryById(Long id) throws Exception;

    InventoryDTO getInventoryByProductIdAndBranchId(Long productId, Long branchId);

    List<InventoryDTO> getAllInventoryByBranchId(Long branchId);

    // Multi-tenant methods
    List<InventoryDTO> getAllInventory(User user) throws Exception;

    List<InventoryDTO> getAllInventoryByBranchId(Long branchId, User user) throws Exception;

    InventoryDTO getInventoryById(Long id, User user) throws Exception;

    InventoryDTO getInventoryByProductIdAndBranchId(
            Long productId,
            Long branchId,
            User user
    ) throws Exception;
    void addStock(
            Long productId,
            Long branchId,
            Integer quantity
    ) throws Exception;
}
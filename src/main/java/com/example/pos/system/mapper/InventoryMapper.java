package com.example.pos.system.mapper;

import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Product;
import com.example.pos.system.payload.dto.InventoryDTO;

public class InventoryMapper {

    public static InventoryDTO toDTO(Inventory inventory) {

        return InventoryDTO.builder()
                .id(inventory.getId())
                .branchId(inventory.getBranch().getId())
                .branch(BranchMapper.toDTO(inventory.getBranch()))
                .productId(inventory.getProduct().getId())
                .product(ProductMapper.toDTO(inventory.getProduct()))
                .quantity(inventory.getQuantity())
                .lastUpdate(inventory.getLastUpdate())
                .build();
    }

    public static Inventory toEntity(
            InventoryDTO dto,
            Branch branch,
            Product product
    ) {

        return Inventory.builder()
                .branch(branch)
                .product(product)
                .quantity(dto.getQuantity())
                .build();
    }
}
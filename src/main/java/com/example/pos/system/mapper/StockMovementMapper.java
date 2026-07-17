package com.example.pos.system.mapper;

import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Product;
import com.example.pos.system.modal.StockMovement;
import com.example.pos.system.payload.dto.StockMovementDTO;

import java.time.LocalDateTime;

public class StockMovementMapper {

    public static StockMovement toEntity(
            StockMovementDTO dto,
            Product product,
            Branch branch
    ) {

        return StockMovement.builder()
                .product(product)
                .branch(branch)
                .quantity(dto.getQuantity())
                .type(dto.getType())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static StockMovementDTO toDTO(
            StockMovement movement
    ) {

        StockMovementDTO dto = new StockMovementDTO();

        dto.setId(movement.getId());
        dto.setProductId(movement.getProduct().getId());
        dto.setBranchId(movement.getBranch().getId());
        dto.setQuantity(movement.getQuantity());
        dto.setType(movement.getType());
        dto.setDescription(movement.getDescription());

        return dto;
    }
}
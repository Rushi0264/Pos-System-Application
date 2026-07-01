package com.example.pos.system.payload.dto;

import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Product;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryDTO {

    private Long id;

    private BranchDTO branch;

    private Long branchId;

    private Long productId;

    private ProductDTO product;

    private Integer quantity;

    private LocalDateTime lastUpdate;
}

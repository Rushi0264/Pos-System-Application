package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockDTO {
    private Long productId;
    private String productName;
    private String branchName;
    private Integer quantity;
    private String status; // "Critical" or "Low"
}
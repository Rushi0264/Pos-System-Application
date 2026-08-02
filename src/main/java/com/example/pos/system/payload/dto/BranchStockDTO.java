package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchStockDTO {
    private Long branchId;
    private String branchName;
    private Integer quantity;
}
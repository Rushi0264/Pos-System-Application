package com.example.pos.system.payload.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseItemDTO {

    private Long id;

    private Long purchaseId;

    private Long productId;

    private ProductDTO product;

    private Integer quantity;

    private Double purchasePrice;

    private Double totalPrice;
}
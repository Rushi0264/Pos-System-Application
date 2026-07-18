package com.example.pos.system.payload.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductDTO {

    private Long id;

    private String name;

    private String sku;

    private String description;

    private Double mrp;

    private Double sellingPrice;

    private String brand;

    private String image;

    private CategoryDTO category;

    private Long categoryId;

    private Long storeId;

    // ===== Stock related (not persisted on Product entity) =====

    // Used only while CREATING a product — which branch to stock it in
    private Long branchId;

    // Used only while CREATING a product — initial quantity for that branch
    private Integer quantity;

    // Used only while DISPLAYING a product — total stock across all branches
    private Integer totalStock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}

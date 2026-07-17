package com.example.pos.system.payload.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderItemDTO {


    private Long id;

    private Integer quantity;

    private String productName;

    private Double price;


    private ProductDTO product;

    private Long productId;


    private Long orderId;

}
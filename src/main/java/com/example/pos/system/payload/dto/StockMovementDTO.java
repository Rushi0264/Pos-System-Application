package com.example.pos.system.payload.dto;


import com.example.pos.system.domain.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {


    private Long id;


    private Long productId;


    private Long branchId;


    private Integer quantity;


    private MovementType type;


    private String description;

}
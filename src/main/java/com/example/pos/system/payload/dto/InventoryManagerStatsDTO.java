package com.example.pos.system.payload.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryManagerStatsDTO {
    private int totalStock;
    private int lowStockCount;
    private int outOfStockCount;
    private long incomingOrders;
}
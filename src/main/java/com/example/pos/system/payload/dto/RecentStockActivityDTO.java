package com.example.pos.system.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentStockActivityDTO {
    private String productName;
    private String branchName;
    private Integer quantity;
    private LocalDateTime lastUpdate;
}
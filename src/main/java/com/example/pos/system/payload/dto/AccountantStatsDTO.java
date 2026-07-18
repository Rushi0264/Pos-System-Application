package com.example.pos.system.payload.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountantStatsDTO {
    private double totalRevenue;
    private double todayRevenue;
    private double totalPurchases;
    private double totalRefunds;
}
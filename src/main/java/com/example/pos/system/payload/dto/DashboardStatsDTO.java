package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private double todaySales;
    private long orders;
    private long stock;
    private long customers;
}
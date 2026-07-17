package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesDTO {
    private String month; // "Jan", "Feb"...
    private Double totalSales;
}
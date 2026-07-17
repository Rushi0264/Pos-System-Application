package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private String type; // CASH, CARD, UPI, WALLET
    private Long count;
    private Double percentage;
}
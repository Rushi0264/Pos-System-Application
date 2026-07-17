package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    private String type; // "ORDER" or "REFUND"

    private String title;

    private String description;

    private Double amount;

    private String actor; // cashier name

    private LocalDateTime timestamp;
}
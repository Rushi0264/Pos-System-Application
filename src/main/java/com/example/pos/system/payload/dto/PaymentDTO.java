package com.example.pos.system.payload.dto;


import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.PaymentType;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {


    private Long id;


    private Long orderId;


    private Double amount;


    private PaymentType paymentType;


    private PaymentStatus status;


    private LocalDateTime createdAt;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

}
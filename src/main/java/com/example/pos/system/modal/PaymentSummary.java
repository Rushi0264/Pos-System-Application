package com.example.pos.system.modal;

import com.example.pos.system.domain.PaymentType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSummary {

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private Double totalAmount;
    private int transactionCount;
    private double percentage;
}
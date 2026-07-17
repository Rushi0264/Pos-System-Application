package com.example.pos.system.modal;

import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    @Column(nullable = false)
    private Double amount;


    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;


    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){

        createdAt = LocalDateTime.now();

    }

}
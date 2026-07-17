package com.example.pos.system.modal;

import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import com.example.pos.system.domain.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double totalAmount;

    private Double subtotal;

    private Double discountAmount;

    private Double taxAmount;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cashier_id")
    private User cashier;

    @ManyToOne
    private Customer customer;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private Payment payment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}


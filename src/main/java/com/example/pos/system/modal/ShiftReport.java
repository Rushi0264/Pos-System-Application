package com.example.pos.system.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    private Double totalSale;
    private Double totalRefunds;
    private Double netSale;
    private int totalOrders;

    @ManyToOne
    private User cashier;

    @ManyToOne
    private Branch branch;

    @ElementCollection
    @CollectionTable(
            name = "shift_report_payment_summary",
            joinColumns = @JoinColumn(name = "shift_report_id")
    )
    private List<PaymentSummary> paymentSummaries;

    @OneToMany
    @JoinTable(
            name = "shift_report_top_selling_products",
            joinColumns = @JoinColumn(name = "shift_report_id"),
            inverseJoinColumns = @JoinColumn(name = "top_selling_products_id")
    )
    private List<Product> topSellingProducts;

    @OneToMany
    private List<Order> recentOrders;

    @OneToMany(mappedBy = "shiftReport", cascade = CascadeType.ALL)
    private List<Refund> refunds;

}

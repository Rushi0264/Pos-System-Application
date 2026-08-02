package com.example.pos.system.modal;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_branch_product",
                        columnNames = {
                                "branch_id",
                                "product_id"
                        }
                )
        }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    private LocalDateTime lastUpdate;

    @Builder.Default
    @Column(nullable = false)
    private Integer lowStockThreshold = 5;

    @PrePersist
    @PreUpdate
    protected void onUpdate(){
        lastUpdate = LocalDateTime.now();
    }
}

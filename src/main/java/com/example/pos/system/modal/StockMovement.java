package com.example.pos.system.modal;


import com.example.pos.system.domain.MovementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMovement {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @ManyToOne
    private Product product;



    @ManyToOne
    private Branch branch;



    private Integer quantity;


    @Enumerated(EnumType.STRING)
    private MovementType type;



    @ManyToOne
    private Store store;


    private String description;


    private LocalDateTime createdAt;



    @PrePersist
    protected void onCreate(){

        createdAt = LocalDateTime.now();

    }

}
package com.example.pos.system.modal;


import com.example.pos.system.domain.StoreStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
public class Store {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private String brand;



    private String description;



    private String storeType;


    private String name;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;



    private LocalDateTime createdAt;



    private LocalDateTime updatedAt;



    @Embedded
    private StoreContact contact;



    @OneToMany(
            mappedBy = "store"
    )
    @JsonIgnore
    private List<User> users =
            new ArrayList<>();





    @PrePersist
    public void create(){

        createdAt = LocalDateTime.now();


        if(status==null){

            status =
                    StoreStatus.ACTIVE;

        }

    }



    @PreUpdate
    public void update(){

        updatedAt =
                LocalDateTime.now();

    }


}
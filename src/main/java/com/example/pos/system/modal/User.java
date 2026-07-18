package com.example.pos.system.modal;


import com.example.pos.system.domain.UserRole;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
public class User {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String fullName;



    @Column(unique = true)
    private String email;



    private String password;



    private String phone;




    @Enumerated(EnumType.STRING)
    private UserRole role;



    private LocalDateTime createdAt;



    private LocalDateTime updatedAt;



    private LocalDateTime lastLogin;





    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name="store_id"
    )
    @JsonIgnore
    private Store store;





    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name="branch_id"
    )
    @JsonIgnore
    private Branch branch;
}
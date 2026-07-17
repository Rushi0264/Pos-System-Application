package com.example.pos.system.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private String address;

    private Long storeId;

    private Long branchId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
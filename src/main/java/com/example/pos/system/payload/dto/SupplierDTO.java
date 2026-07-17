package com.example.pos.system.payload.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SupplierDTO {

    private Long id;

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String gstNumber;

    private String address;

    private Long storeId;

    private StoreDTO store;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.modal.StoreContact;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreDTO {

    private Long id;

    private String brand;

    private UserDto storeAdmin;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String description;
    private String storeType;

    private StoreStatus status;
    private StoreContact contact;


}

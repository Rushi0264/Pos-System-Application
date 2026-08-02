package com.example.pos.system.payload.dto;

import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class BranchDTO {

    private Long id;

    private String name;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String phone;

    private String email;

    private List<String> workingDays;

    private LocalTime openTime;
    private LocalTime closeTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private StoreDTO store;

    private Long storeId;

    private UserDto manager;

}

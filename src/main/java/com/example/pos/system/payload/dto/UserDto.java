package com.example.pos.system.payload.dto;


import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.domain.UserRole;

import lombok.Data;


import java.time.LocalDateTime;



@Data
public class UserDto {



    private Long id;


    private String fullName;


    private String email;

    private StoreStatus storeStatus;

    private String password;


    private String phone;


    private UserRole role;



    private Long storeId;



    private Long branchId;

    private String storeBrand;

    private String branchName;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private LocalDateTime lastLogin;


}
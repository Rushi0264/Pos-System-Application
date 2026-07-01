package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.UserRole;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long id;
    private String fullName;
    private String email;

    private String phone;
    private UserRole role;

    private String password;

    private Long branchId;
    private Long storeId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
}

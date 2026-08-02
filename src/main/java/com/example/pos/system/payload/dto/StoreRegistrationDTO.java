package com.example.pos.system.payload.dto;

import lombok.Data;

@Data
public class StoreRegistrationDTO {

    // Store Info
    private String brand;
    private String storeType;
    private String description;
    private String address;
    private String phone;   // store contact phone
    private String email;   // store contact email

    // Owner (Store Admin) Info
    private String ownerFullName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerPassword;
}
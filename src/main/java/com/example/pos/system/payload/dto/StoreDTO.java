package com.example.pos.system.payload.dto;


import com.example.pos.system.domain.StoreStatus;

import com.example.pos.system.modal.StoreContact;
import lombok.Data;


@Data
public class StoreDTO {



    private Long id;


    private String brand;


    private String description;


    private String storeType;


    private StoreStatus status;


    private StoreContact contact;


}
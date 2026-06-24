package com.example.pos.system.payload.dto;

import com.example.pos.system.modal.Store;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    private Long id;

    private String name;

    //private Store store;

    private Long storeId;
}

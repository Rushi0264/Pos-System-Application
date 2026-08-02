package com.example.pos.system.payload.dto;

import lombok.Data;

public class StoreRequestDTO {

    @Data
    public class StoreApprovalRequest {
        private String reason;
    }
}

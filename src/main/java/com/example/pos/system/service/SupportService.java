package com.example.pos.system.service;

import com.example.pos.system.payload.dto.SupportRequest;

public interface SupportService {

    void sendSupportRequest(
            String userEmail,
            SupportRequest request
    );
}
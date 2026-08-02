package com.example.pos.system.service;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String message
    );
}
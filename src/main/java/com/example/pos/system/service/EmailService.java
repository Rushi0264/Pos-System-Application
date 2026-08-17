package com.example.pos.system.service;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String message
    );

    void sendEmailWithAttachment(
            String to,
            String subject,
            String message,
            byte[] attachmentBytes,
            String attachmentFileName
    );
}
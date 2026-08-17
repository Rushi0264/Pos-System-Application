package com.example.pos.system.service.impl;

import com.example.pos.system.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(
            String to,
            String subject,
            String message) {

        SimpleMailMessage mailMessage =
                new SimpleMailMessage();

        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendEmailWithAttachment(
            String to,
            String subject,
            String message,
            byte[] attachmentBytes,
            String attachmentFileName) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // true -> multipart message (needed for attachment)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);

            if (attachmentBytes != null && attachmentBytes.length > 0) {
                helper.addAttachment(
                        attachmentFileName,
                        new org.springframework.core.io.ByteArrayResource(attachmentBytes)
                );
            }

            mailSender.send(mimeMessage);
            log.info("Email with attachment sent to {} — subject: {}", to, subject);

        } catch (Exception e) {
            log.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
        }
    }
}
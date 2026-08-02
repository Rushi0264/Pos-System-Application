package com.example.pos.system.service.impl;

import com.example.pos.system.modal.User;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.payload.dto.SupportRequest;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.EmailService;
import com.example.pos.system.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Override
    public void sendSupportRequest(
            String userEmail,
            SupportRequest request) {

        User superAdmin = userRepository
                .findFirstByRole(UserRole.ROLE_SUPER_ADMIN)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Super Admin not found"
                        )
                );

        String superAdminEmail =
                superAdmin.getEmail();

        String emailBody =
                "Hello Super Admin,\n\n"
                        + "A Store User has requested support.\n\n"
                        + "Store User Email: "
                        + userEmail
                        + "\n\n"
                        + "Request Type: "
                        + request.getSubject()
                        + "\n\n"
                        + "Message:\n"
                        + request.getMessage()
                        + "\n\n"
                        + "Please review the store account and take necessary action.";

        emailService.sendEmail(
                superAdminEmail,
                request.getSubject(),
                emailBody
        );
    }
}
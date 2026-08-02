package com.example.pos.system.controller;


import com.example.pos.system.payload.dto.SupportRequest;
import com.example.pos.system.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/contact")
    public ResponseEntity<?> contactSupport(
            @RequestBody SupportRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity
                    .status(401)
                    .body("User is not authenticated");
        }

        String userEmail = authentication.getName();

        supportService.sendSupportRequest(
                userEmail,
                request
        );

        return ResponseEntity.ok(
                "Support request sent successfully to Super Admin"
        );
    }
}
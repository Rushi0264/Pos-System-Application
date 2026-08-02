package com.example.pos.system.controller;

import com.example.pos.system.payload.dto.NotificationDTO;
import com.example.pos.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<NotificationDTO>> getByBranch(
            @PathVariable Long branchId
    ) throws Exception {
        return ResponseEntity.ok(notificationService.getNotificationsByBranch(branchId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<NotificationDTO>> getByStore(
            @PathVariable Long storeId
    ) throws Exception {
        return ResponseEntity.ok(notificationService.getNotificationsByStore(storeId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id
    ) throws Exception {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
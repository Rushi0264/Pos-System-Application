package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String message;
    private Long branchId;
    private String branchName;
    private Long productId;
    private String productName;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
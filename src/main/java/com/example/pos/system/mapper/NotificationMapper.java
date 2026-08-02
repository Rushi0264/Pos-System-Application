package com.example.pos.system.mapper;

import com.example.pos.system.modal.Notification;
import com.example.pos.system.payload.dto.NotificationDTO;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .branchId(n.getBranch() != null ? n.getBranch().getId() : null)
                .branchName(n.getBranch() != null ? n.getBranch().getName() : null)
                .productId(n.getProduct() != null ? n.getProduct().getId() : null)
                .productName(n.getProduct() != null ? n.getProduct().getName() : null)
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
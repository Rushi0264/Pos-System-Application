package com.example.pos.system.service;

import com.example.pos.system.modal.Inventory;
import com.example.pos.system.payload.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    void checkAndNotifyLowStock(Inventory inventory) throws Exception;

    List<NotificationDTO> getNotificationsByBranch(Long branchId) throws Exception;

    List<NotificationDTO> getNotificationsByStore(Long storeId) throws Exception;

    void markAsRead(Long notificationId) throws Exception;
}
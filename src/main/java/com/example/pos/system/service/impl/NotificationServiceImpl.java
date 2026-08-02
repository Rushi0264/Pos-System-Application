package com.example.pos.system.service.impl;

import com.example.pos.system.domain.NotificationStatus;
import com.example.pos.system.domain.NotificationType;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.NotificationMapper;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Notification;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.NotificationDTO;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.repository.BranchRepository;
import com.example.pos.system.repository.NotificationRepository;
import com.example.pos.system.service.NotificationService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final BranchRepository branchRepository;
    private final UserService userService;

    @Override
    public void checkAndNotifyLowStock(Inventory inventory) throws Exception {

        if (inventory.getQuantity() == null || inventory.getLowStockThreshold() == null) {
            return;
        }

        if (inventory.getQuantity() <= inventory.getLowStockThreshold()) {

            String message = "Low stock alert: " + inventory.getProduct().getName()
                    + " has only " + inventory.getQuantity() + " units left in "
                    + inventory.getBranch().getName();

            // Check if an ACTIVE low-stock notification already exists for this branch+product
            var existingActive = notificationRepository
                    .findByBranchIdAndProductIdAndTypeAndStatus(
                            inventory.getBranch().getId(),
                            inventory.getProduct().getId(),
                            NotificationType.LOW_STOCK,
                            NotificationStatus.ACTIVE
                    );

            if (existingActive.isPresent()) {

                // Already active — just refresh the message with current quantity
                Notification existing = existingActive.get();
                existing.setMessage(message);
                notificationRepository.save(existing);

            } else {

                Notification notification = Notification.builder()
                        .type(NotificationType.LOW_STOCK)
                        .message(message)
                        .branch(inventory.getBranch())
                        .product(inventory.getProduct())
                        .status(NotificationStatus.ACTIVE)
                        .build();

                notificationRepository.save(notification);
            }

        } else {
            // Stock is back above threshold — resolve any existing active notification
            notificationRepository
                    .findByBranchIdAndProductIdAndTypeAndStatus(
                            inventory.getBranch().getId(),
                            inventory.getProduct().getId(),
                            NotificationType.LOW_STOCK,
                            NotificationStatus.ACTIVE
                    )
                    .ifPresent(existing -> {
                        existing.setStatus(NotificationStatus.RESOLVED);
                        notificationRepository.save(existing);
                    });
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsByBranch(Long branchId) throws Exception {

        User currentUser = userService.getCurrentUser();

        checkBranchAccess(currentUser, branchId);

        return notificationRepository.findByBranchIdAndStatusOrderByCreatedAtDesc(
                        branchId, NotificationStatus.ACTIVE)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getNotificationsByStore(Long storeId) throws Exception {

        User currentUser = userService.getCurrentUser();

        checkStoreAccess(currentUser, storeId);

        return notificationRepository.findByBranch_Store_IdAndStatusOrderByCreatedAtDesc(
                        storeId, NotificationStatus.ACTIVE)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void checkBranchAccess(User user, Long branchId) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN
                || user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {
            return;
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new Exception("Branch not found"));

        if (user.getRole() == UserRole.ROLE_STORE_ADMIN
                || user.getRole() == UserRole.ROLE_ACCOUNTANT) {

            if (user.getStore() != null
                    && user.getStore().getId().equals(branch.getStore().getId())) {
                return;
            }
        }

        if (user.getBranch() != null && user.getBranch().getId().equals(branchId)) {
            return;
        }

        throw new UserException("You cannot access notifications for this branch");
    }

    private void checkStoreAccess(User user, Long storeId) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN
                || user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {
            return;
        }

        if (user.getStore() != null && user.getStore().getId().equals(storeId)) {
            return;
        }

        throw new UserException("You cannot access notifications for this store");
    }

    @Override
    public void markAsRead(Long notificationId) throws Exception {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
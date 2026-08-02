package com.example.pos.system.repository;

import com.example.pos.system.domain.NotificationStatus;
import com.example.pos.system.domain.NotificationType;
import com.example.pos.system.modal.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByBranchIdAndStatusOrderByCreatedAtDesc(
            Long branchId, NotificationStatus status);

    List<Notification> findByBranch_Store_IdAndStatusOrderByCreatedAtDesc(
            Long storeId, NotificationStatus status);

    Optional<Notification> findByBranchIdAndProductIdAndTypeAndStatus(
            Long branchId, Long productId, NotificationType type, NotificationStatus status
    );


    boolean existsByBranchIdAndProductIdAndTypeAndStatus(
            Long branchId, Long productId, NotificationType type, NotificationStatus status);

}
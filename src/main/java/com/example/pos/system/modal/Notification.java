package com.example.pos.system.modal;

import com.example.pos.system.domain.NotificationStatus;
import com.example.pos.system.domain.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Product product;

    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
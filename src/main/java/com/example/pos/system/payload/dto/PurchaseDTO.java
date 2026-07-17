package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.PaymentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDTO {

    private Long id;

    private Long supplierId;

    private SupplierDTO supplier;

    private Long storeId;

    private Long branchId;

    private Long createdById;

    private Double totalAmount;

    private PaymentType paymentType;

    private String invoiceNumber;

    private String remarks;

    private List<PurchaseItemDTO> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.ShiftReport;
import com.example.pos.system.modal.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefundDTO {

    private Long id;

    private OrderDTO order;
    private Long orderId;

    private String reason;

    private Double amount;

    //private ShiftReport shiftReport;
    private Long shiftReportId;

    private UserDto cashier;
    private String cashierName;

    private Branch branch;
    private Long branchId;

    private PaymentType paymentType;

    private LocalDateTime createdAt;

}

package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDTO {

    private Long id;

    private Double totalAmount;

    private LocalDateTime createdAt;

    private String customerName;

    private String customerPhone;

    private Long branchId;

    private Long customerId;

    private BranchDTO branch;

    private Double subtotal;

    private Double discountAmount;

    private Double taxAmount;

    private UserDto cashier;

    private Long cashierId;

    private PaymentType paymentType;

    private OrderStatus status;

    private CustomerDTO customer;

    private List<OrderItemDTO> items;

}
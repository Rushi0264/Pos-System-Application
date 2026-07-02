package com.example.pos.system.payload.dto;

import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Customer;
import com.example.pos.system.modal.OrderItem;
import com.example.pos.system.modal.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDTO {

    private Long id;

    private Double totalAmount;

    private LocalDateTime createdAt;

    private Long branchId;
    private Long customerId;

    private BranchDTO branch;

    private UserDto cashier;

    private Customer customer;

    private PaymentType paymentType;

    private List<OrderItemDTO> items;

}

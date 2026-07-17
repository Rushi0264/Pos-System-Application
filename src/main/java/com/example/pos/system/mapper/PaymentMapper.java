package com.example.pos.system.mapper;


import com.example.pos.system.modal.Customer;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.Payment;
import com.example.pos.system.payload.dto.PaymentDTO;


public class PaymentMapper {


    public static PaymentDTO toDTO(Payment payment){

        Order order = payment.getOrder();
        Customer customer = (order != null) ? order.getCustomer() : null;

        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(order != null ? order.getId() : null)
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .customerName(customer != null ? customer.getFullName() : null)
                .customerPhone(customer != null ? customer.getPhone() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .build();
    }



    public static Payment toEntity(
            PaymentDTO dto,
            Order order
    ){

        return Payment.builder()
                .order(order)
                .amount(dto.getAmount())
                .paymentType(dto.getPaymentType())
                .status(dto.getStatus())
                .build();

    }

}
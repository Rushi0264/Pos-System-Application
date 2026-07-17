package com.example.pos.system.mapper;


import com.example.pos.system.modal.Order;
import com.example.pos.system.payload.dto.OrderDTO;

import java.util.Collections;


public class OrderMapper {


    public static OrderDTO toDTO(Order order) {


        OrderDTO dto = OrderDTO.builder()

                .id(order.getId())

                .totalAmount(
                        order.getTotalAmount()
                )
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                //.paymentStatus(order.getPaymentStatus())

                .createdAt(
                        order.getCreatedAt()
                )

                .paymentType(
                        order.getPaymentType()
                )

                .status(
                        order.getStatus()
                )


                // =========================
                // CUSTOMER
                // =========================

                .customerId(
                        order.getCustomer() != null
                                ? order.getCustomer().getId()
                                : null
                )


                .customerName(
                        order.getCustomer() != null
                                ? order.getCustomer().getFullName()
                                : "Walk-in Customer"
                )


                .customerPhone(
                        order.getCustomer() != null
                                ? order.getCustomer().getPhone()
                                : null
                )


                // =========================
                // BRANCH
                // =========================

                .branchId(
                        order.getBranch() != null
                                ? order.getBranch().getId()
                                : null
                )


                .branch(
                        order.getBranch() != null
                                ? BranchMapper.toDTO(order.getBranch())
                                : null
                )


                // =========================
                // CASHIER
                // =========================

                .cashierId(
                        order.getCashier() != null
                                ? order.getCashier().getId()
                                : null
                )


                .cashier(
                        order.getCashier() != null
                                ? UserMapper.toDTO(order.getCashier())
                                : null
                )


                // =========================
                // ORDER ITEMS + PRODUCTS
                // =========================

                .items(
                        order.getItems() != null
                                ?
                                order.getItems()
                                        .stream()
                                        .map(OrderItemMapper::toDTO)
                                        .toList()
                                :
                                Collections.emptyList()
                )


                .build();


        return dto;
    }
}
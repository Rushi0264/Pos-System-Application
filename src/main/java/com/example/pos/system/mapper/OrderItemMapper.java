package com.example.pos.system.mapper;

import com.example.pos.system.modal.OrderItem;
import com.example.pos.system.payload.dto.OrderItemDTO;

public class OrderItemMapper {

    public static OrderItemDTO toDTO(OrderItem item) {

        if (item == null) {
            return null;
        }

        return OrderItemDTO.builder()

                .id(item.getId())

                .quantity(item.getQuantity())

                .price(item.getPrice())

                .productId(
                        item.getProduct() != null
                                ? item.getProduct().getId()
                                : null
                )

                // ADD THIS
                .productName(
                        item.getProduct() != null
                                ? item.getProduct().getName()
                                : null
                )

                .product(
                        item.getProduct() != null
                                ? ProductMapper.toDTO(item.getProduct())
                                : null
                )

                .orderId(
                        item.getOrder() != null
                                ? item.getOrder().getId()
                                : null
                )

                .build();
    }
}
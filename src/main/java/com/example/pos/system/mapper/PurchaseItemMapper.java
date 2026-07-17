package com.example.pos.system.mapper;

import com.example.pos.system.modal.Product;
import com.example.pos.system.modal.Purchase;
import com.example.pos.system.modal.PurchaseItem;
import com.example.pos.system.payload.dto.PurchaseItemDTO;

public class PurchaseItemMapper {

    public static PurchaseItemDTO toDTO(PurchaseItem item){

        return PurchaseItemDTO.builder()
                .id(item.getId())
                .purchaseId(
                        item.getPurchase()!=null ?
                                item.getPurchase().getId() : null
                )
                .productId(
                        item.getProduct()!=null ?
                                item.getProduct().getId() : null
                )
                .product(
                        item.getProduct()!=null ?
                                ProductMapper.toDTO(item.getProduct()) : null
                )
                .quantity(item.getQuantity())
                .purchasePrice(item.getPurchasePrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public static PurchaseItem toEntity(
            PurchaseItemDTO dto,
            Purchase purchase,
            Product product
    ){

        Double totalPrice = dto.getPurchasePrice() * dto.getQuantity();

        return PurchaseItem.builder()
                .purchase(purchase)
                .product(product)
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .totalPrice(totalPrice)
                .build();
    }

}
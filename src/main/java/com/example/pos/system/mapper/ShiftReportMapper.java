package com.example.pos.system.mapper;

import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.Product;
import com.example.pos.system.modal.Refund;
import com.example.pos.system.modal.ShiftReport;
import com.example.pos.system.payload.dto.OrderDTO;
import com.example.pos.system.payload.dto.ProductDTO;
import com.example.pos.system.payload.dto.RefundDTO;
import com.example.pos.system.payload.dto.ShiftReportDTO;
import com.example.pos.system.mapper.BranchMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ShiftReportMapper {

    public static ShiftReportDTO toDTO(ShiftReport entity) {
        return ShiftReportDTO.builder()
                .id(entity.getId())
                .shiftEnd(entity.getShiftEnd())
                .shiftStart(entity.getShiftStart())
                .totalSale(entity.getTotalSale())
                .totalRefunds(entity.getTotalRefunds())
                .netSale(entity.getNetSale())
                .totalOrders(entity.getTotalOrders())
                .cashier(UserMapper.toDTO(entity.getCashier()))
                .cashierId(entity.getCashier().getId())
                .branchId(entity.getBranch().getId())
                .branch(BranchMapper.toDTO(entity.getBranch()))
                .recentOrders(mapOrders(entity.getRecentOrders()))
                .topSellingProducts(mapProducts(entity.getTopSellingProducts()))
                .refunds(mapRefunds(entity.getRefunds()))
                .paymentSummaries(entity.getPaymentSummaries())
                .build();
    }

    private static List<RefundDTO> mapRefunds(List<Refund> refunds) {
        if (refunds == null || refunds.isEmpty()) {
            return null;
        }

        return refunds.stream().map(RefundMapper::toDTO).collect(Collectors.toList());
    }

    private static List<ProductDTO> mapProducts(List<Product> topSellingProducts) {
        if (topSellingProducts == null || topSellingProducts.isEmpty()) {
            return null;
        }

        return topSellingProducts.stream().map(ProductMapper::toDTO).collect(Collectors.toList());
    }

    private static List<OrderDTO> mapOrders(List<Order> recentOrders) {
        if (recentOrders == null || recentOrders.isEmpty()) {
            return null;
        }

        return recentOrders.stream().map(OrderMapper::toDTO).collect(Collectors.toList());
    }
}

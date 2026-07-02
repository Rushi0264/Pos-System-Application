package com.example.pos.system.payload.dto;

import com.example.pos.system.modal.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ShiftReportDTO {

    private Long id;

    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    private Double totalSale;
    private Double totalRefunds;
    private Double netSale;
    private int totalOrders;

    private UserDto cashier;
    private Long cashierId;

    private BranchDTO branch;
    private Long branchId;

    private List<PaymentSummary> paymentSummaries;

    private List<ProductDTO> topSellingProducts;

    private List<OrderDTO> recentOrders;

    private List<RefundDTO> refunds;
}

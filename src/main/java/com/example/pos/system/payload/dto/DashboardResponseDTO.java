package com.example.pos.system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    // Counts
    private Long totalStores;

    private Long totalBranches;

    private Long totalUsers;

    private Long totalEmployees;

    private Long activeStores;
    private Long pendingStores;

    private Long totalProducts;

    private Long totalCategories;

    private Long totalCustomers;

    private Long totalOrders;

    // Revenue
    private Double totalRevenue;

    private Double todayRevenue;

    // Today's Activity
    private Long todayOrders;

    private Long todayCustomers;

    // Inventory
    private Long lowStockProducts;
}
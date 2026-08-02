package com.example.pos.system.controller;

import com.example.pos.system.payload.dto.*;
import com.example.pos.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/super-admin")
    public DashboardResponseDTO getDashboard() {

        return dashboardService.getSuperAdminDashboard();

    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<OrderDTO>> getRecentOrders() {
        return ResponseEntity.ok(dashboardService.getRecentOrders());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<LowStockDTO>> getLowStock() {
        return ResponseEntity.ok(dashboardService.getLowStockProducts());
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<List<MonthlySalesDTO>> getMonthlySales() {
        return ResponseEntity.ok(dashboardService.getMonthlySales());
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethods() {
        return ResponseEntity.ok(dashboardService.getPaymentMethodBreakdown());
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
    @GetMapping("/inventory-manager-stats")
    public ResponseEntity<InventoryManagerStatsDTO> getInventoryManagerStats() {
        return ResponseEntity.ok(dashboardService.getInventoryManagerStats());
    }

    @GetMapping("/recent-stock-activity")
    public ResponseEntity<List<RecentStockActivityDTO>> getRecentStockActivity() {
        return ResponseEntity.ok(dashboardService.getRecentStockActivity());
    }

    @GetMapping("/accountant-stats")
    public ResponseEntity<AccountantStatsDTO> getAccountantStats() {
        return ResponseEntity.ok(dashboardService.getAccountantStats());
    }


    @GetMapping("/super-admin/recent-stores")
    public ResponseEntity<List<StoreDTO>> getRecentStores() {
        return ResponseEntity.ok(dashboardService.getRecentStores());
    }

    @GetMapping("/super-admin/store-growth")
    public ResponseEntity<List<MonthlySalesDTO>> getStoreGrowth() {
        return ResponseEntity.ok(dashboardService.getStoreGrowth());
    }

    @GetMapping("/super-admin/store-status")
    public ResponseEntity<List<PaymentMethodDTO>> getStoreStatusBreakdown() {
        return ResponseEntity.ok(dashboardService.getStoreStatusBreakdown());
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<ActivityDTO>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
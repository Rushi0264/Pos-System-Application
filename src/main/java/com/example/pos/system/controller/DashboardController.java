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
}
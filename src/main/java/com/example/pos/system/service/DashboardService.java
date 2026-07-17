package com.example.pos.system.service;


import com.example.pos.system.payload.dto.*;

import java.util.List;

public interface DashboardService {

    DashboardResponseDTO getSuperAdminDashboard();

    List<OrderDTO> getRecentOrders();

    List<LowStockDTO> getLowStockProducts();

    List<MonthlySalesDTO> getMonthlySales();

    List<PaymentMethodDTO> getPaymentMethodBreakdown();

    List<ActivityDTO> getRecentActivity();

}
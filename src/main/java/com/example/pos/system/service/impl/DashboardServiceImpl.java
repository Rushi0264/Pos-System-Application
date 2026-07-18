package com.example.pos.system.service.impl;

import com.example.pos.system.mapper.OrderMapper;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.*;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    public List<ActivityDTO> getRecentActivity() {
        Pageable top10 = PageRequest.of(0, 10);

        List<ActivityDTO> orderActivities = orderRepository
                .findAll(top10)
                .stream()
                .map(o -> ActivityDTO.builder()
                        .type("ORDER")
                        .title("New order placed")
                        .description("Order #" + o.getId() + " - " +
                                (o.getCustomer() != null ? o.getCustomer().getFullName() : "Walk-in Customer"))
                        .amount(o.getTotalAmount())
                        .actor(o.getCashier() != null ? o.getCashier().getFullName() : null)
                        .timestamp(o.getCreatedAt())
                        .build())
                .toList();
        List<ActivityDTO> refundActivities = refundRepository
                .findRecentRefunds(top10)
                .stream()
                .map(r -> ActivityDTO.builder()
                        .type("REFUND")
                        .title("Refund issued")
                        .description("Refund for Order #" +
                                (r.getOrder() != null ? r.getOrder().getId() : "-") +
                                (r.getReason() != null ? " - " + r.getReason() : ""))
                        .amount(r.getAmount())
                        .actor(r.getCashier() != null ? r.getCashier().getFullName() : null)
                        .timestamp(r.getCreatedAt())
                        .build())
                .toList();

        return java.util.stream.Stream.concat(orderActivities.stream(), refundActivities.stream())
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(10)
                .toList();
    }

    @Override
    public DashboardResponseDTO getSuperAdminDashboard() {

        LocalDateTime start = LocalDate.now().atStartOfDay();

        LocalDateTime end = LocalDate.now().atTime(23,59,59);
        DashboardResponseDTO dto = new DashboardResponseDTO();

        dto.setTotalStores(storeRepository.count());

        dto.setTotalBranches(branchRepository.count());

        dto.setTotalUsers(userRepository.count());

        dto.setTotalProducts(productRepository.count());

        dto.setTotalCustomers(customerRepository.count());

        dto.setTotalOrders(orderRepository.count());

        dto.setTotalRevenue(orderRepository.getTotalRevenue());

        dto.setTodayRevenue(
                orderRepository.getTodayRevenue(start, end)
        );

        dto.setTotalEmployees(
                userRepository.countEmployees()
        );

        dto.setTodayOrders(
                orderRepository.getTodayOrders(start, end)
        );

        dto.setLowStockProducts(
                inventoryRepository.getLowStockProducts()
        );

        dto.setTotalCategories(0L);

        dto.setTodayCustomers(0L);

        return dto;
    }

    @Override
    public List<OrderDTO> getRecentOrders() {
        return orderRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public List<LowStockDTO> getLowStockProducts() {
        return inventoryRepository.findLowStockList()
                .stream()
                .map(inv -> new LowStockDTO(
                        inv.getProduct().getId(),
                        inv.getProduct().getName(),
                        inv.getBranch().getName(),
                        inv.getQuantity(),
                        inv.getQuantity() < 10 ? "Critical" : "Low"
                ))
                .toList();
    }

    @Override
    public List<MonthlySalesDTO> getMonthlySales() {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<Order> orders = orderRepository.findAll();

        Map<Integer, Double> salesByMonth = new HashMap<>();
        for (Order o : orders) {
            if (o.getCreatedAt() != null) {
                int monthIndex = o.getCreatedAt().getMonthValue() - 1;
                salesByMonth.merge(monthIndex, o.getTotalAmount() == null ? 0 : o.getTotalAmount(), Double::sum);
            }
        }

        List<MonthlySalesDTO> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(new MonthlySalesDTO(months[i], salesByMonth.getOrDefault(i, 0.0)));
        }
        return result;
    }

    @Override
    public List<PaymentMethodDTO> getPaymentMethodBreakdown() {
        List<Object[]> raw = orderRepository.getPaymentTypeBreakdown();

        long total = raw.stream().mapToLong(r -> (Long) r[1]).sum();

        return raw.stream()
                .map(r -> new PaymentMethodDTO(
                        r[0].toString(),
                        (Long) r[1],
                        total == 0 ? 0.0 : Math.round(((Long) r[1]) * 100.0 / total)
                ))
                .toList();
    }


    public DashboardStatsDTO getDashboardStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email);
        if (currentUser.getBranch() == null) {
            throw new RuntimeException("User is not assigned to any branch.");
        }

        Long branchId = currentUser.getBranch().getId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        double todaySales = orderRepository.getTodaySalesByBranch(branchId, start, end);
        long orders = orderRepository.countByBranchIdAndCreatedAtToday(branchId, start, end);
        long stock = inventoryRepository.getTotalStockByBranch(branchId);
        long customers = customerRepository.countByBranchId(branchId);

        return new DashboardStatsDTO(todaySales, orders, stock, customers);
    }
    @Override
    public InventoryManagerStatsDTO getInventoryManagerStats() {

        List<Inventory> allInventory = inventoryRepository.findAll();

        int totalStock = allInventory.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        int lowStockCount = (int) allInventory.stream()
                .filter(i -> i.getQuantity() > 0 && i.getQuantity() <= 5)
                .count();

        int outOfStockCount = (int) allInventory.stream()
                .filter(i -> i.getQuantity() == 0)
                .count();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long incomingOrders = purchaseRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null &&
                        p.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        return InventoryManagerStatsDTO.builder()
                .totalStock(totalStock)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .incomingOrders(incomingOrders)
                .build();
    }

    @Override
    public List<RecentStockActivityDTO> getRecentStockActivity() {

        return inventoryRepository.findTop10ByOrderByLastUpdateDesc()
                .stream()
                .map(inv -> RecentStockActivityDTO.builder()
                        .productName(inv.getProduct() != null ? inv.getProduct().getName() : "-")
                        .branchName(inv.getBranch() != null ? inv.getBranch().getName() : "-")
                        .quantity(inv.getQuantity())
                        .lastUpdate(inv.getLastUpdate())
                        .build())
                .toList();
    }

    @Override
    public AccountantStatsDTO getAccountantStats() {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        double totalRevenue = orderRepository.getTotalRevenue();

        double todayRevenue = orderRepository.getTodayRevenue(start, end);

        double totalPurchases = purchaseRepository.findAll().stream()
                .mapToDouble(p -> p.getTotalAmount() == null ? 0 : p.getTotalAmount())
                .sum();

        double totalRefunds = refundRepository.findAll().stream()
                .mapToDouble(r -> r.getAmount() == null ? 0 : r.getAmount())
                .sum();

        return AccountantStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .totalPurchases(totalPurchases)
                .totalRefunds(totalRefunds)
                .build();
    }
}
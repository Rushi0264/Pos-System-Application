package com.example.pos.system.service.impl;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.mapper.OrderMapper;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.Purchase;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.*;
import com.example.pos.system.domain.RefundStatus;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.pos.system.modal.Store;
import com.example.pos.system.mapper.StoreMapper;
import java.util.stream.Collectors;
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

    private Long getCurrentStoreId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email);

        if (currentUser.getStore() != null) {
            return currentUser.getStore().getId();
        }
        if (currentUser.getBranch() != null && currentUser.getBranch().getStore() != null) {
            return currentUser.getBranch().getStore().getId();
        }
        throw new RuntimeException("User is not assigned to any store.");
    }

    @Override
    public List<ActivityDTO> getRecentActivity() {
        Long storeId = getCurrentStoreId();
        Pageable top10 = PageRequest.of(0, 10);

        List<ActivityDTO> orderActivities = orderRepository
                .findByBranch_Store_Id(storeId)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
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
                .findByBranchStoreId(storeId)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
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
        DashboardResponseDTO dto = new DashboardResponseDTO();

        dto.setTotalStores(storeRepository.count());
        dto.setActiveStores(storeRepository.countByStatus(StoreStatus.ACTIVE));
        dto.setPendingStores(
                storeRepository.count() - storeRepository.countByStatus(StoreStatus.ACTIVE)
        );
        dto.setTotalBranches(branchRepository.count());
        dto.setTotalUsers(userRepository.count());

        return dto;
    }

    @Override
    public List<OrderDTO> getRecentOrders() {
        Long storeId = getCurrentStoreId();
        return orderRepository.findTop5ByBranch_Store_IdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public List<LowStockDTO> getLowStockProducts() {
        Long storeId = getCurrentStoreId();
        return inventoryRepository.findLowStockListByStore(storeId)
                .stream()
                .filter(inv -> inv.getQuantity() < 15)
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
        Long storeId = getCurrentStoreId();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<Order> orders = orderRepository.findByBranch_Store_Id(storeId);

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
        Long storeId = getCurrentStoreId();
        List<Object[]> raw = orderRepository.getPaymentTypeBreakdownByStore(storeId);

        long total = raw.stream().mapToLong(r -> (Long) r[1]).sum();

        return raw.stream()
                .map(r -> new PaymentMethodDTO(
                        r[0].toString(),
                        (Long) r[1],
                        total == 0 ? 0.0 : Math.round(((Long) r[1]) * 1000.0 / total)/10.0
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
        long customers = customerRepository.countByBranchIdAndCreatedAtBetween(branchId, start, end);

        return new DashboardStatsDTO(todaySales, orders, stock, customers);
    }

    @Override
    public InventoryManagerStatsDTO getInventoryManagerStats() {
        Long storeId = getCurrentStoreId();
        List<Inventory> storeInventory = inventoryRepository.findByBranchStoreId(storeId);

        int totalStock = storeInventory.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        int lowStockCount = (int) storeInventory.stream()
                .filter(i -> i.getQuantity() > 0 && i.getQuantity() <= 5)
                .count();

        int outOfStockCount = (int) storeInventory.stream()
                .filter(i -> i.getQuantity() == 0)
                .count();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long incomingOrders = purchaseRepository.findByStoreId(storeId).stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(thirtyDaysAgo))
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
        Long storeId = getCurrentStoreId();
        return inventoryRepository.findTop10ByBranch_Store_IdOrderByLastUpdateDesc(storeId)
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
        Long storeId = getCurrentStoreId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        double totalRevenue = orderRepository.getTotalRevenueByStore(storeId);
        double todayRevenue = orderRepository.getTodayRevenueByStore(storeId, start, end);

        double totalPurchases = purchaseRepository.findByStoreId(storeId).stream()
                .mapToDouble(p -> p.getTotalAmount() == null ? 0 : p.getTotalAmount())
                .sum();

        double totalRefunds = refundRepository.findByBranchStoreId(storeId).stream()
                .filter(r -> r.getStatus() == RefundStatus.APPROVED || r.getStatus() == RefundStatus.PROCESSED)
                .mapToDouble(r -> r.getAmount() == null ? 0 : r.getAmount())
                .sum();

        return AccountantStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .totalPurchases(totalPurchases)
                .totalRefunds(totalRefunds)
                .build();
    }

    @Override
    public List<StoreDTO> getRecentStores() {
        return storeRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(StoreMapper::toDTO)
                .toList();
    }

    @Override
    public List<MonthlySalesDTO> getStoreGrowth() {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<Store> stores = storeRepository.findAll();

        Map<Integer, Long> countByMonth = new HashMap<>();
        for (Store s : stores) {
            if (s.getCreatedAt() != null) {
                int idx = s.getCreatedAt().getMonthValue() - 1;
                countByMonth.merge(idx, 1L, Long::sum);
            }
        }

        List<MonthlySalesDTO> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(new MonthlySalesDTO(months[i], countByMonth.getOrDefault(i, 0L).doubleValue()));
        }
        return result;
    }

    @Override
    public List<PaymentMethodDTO> getStoreStatusBreakdown() {
        List<Store> stores = storeRepository.findAll();

        Map<String, Long> countByStatus = stores.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStatus() != null ? s.getStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));

        long total = stores.size();

        return countByStatus.entrySet().stream()
                .map(e -> new PaymentMethodDTO(
                        e.getKey(),
                        e.getValue(),
                        total == 0 ? 0.0 : Math.round(e.getValue() * 1000.0 / total)/10.0
                ))
                .toList();
    }
}
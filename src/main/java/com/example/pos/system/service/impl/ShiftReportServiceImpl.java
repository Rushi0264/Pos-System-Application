package com.example.pos.system.service.impl;

import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.mapper.ShiftReportMapper;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.ShiftReportDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.ShiftReportService;
import com.example.pos.system.domain.RefundStatus;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftReportServiceImpl implements ShiftReportService {

    private final ShiftReportRepository shiftReportRepository;
    private final UserService userService;
    private final BranchRepository branchRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private static final List<RefundStatus> COUNTED_REFUND_STATUSES =
            List.of(RefundStatus.APPROVED, RefundStatus.PROCESSED);

    @Override
    public ShiftReportDTO startShift() throws Exception {

        User currentUser = userService.getCurrentUser();
        LocalDateTime shiftStart = LocalDateTime.now();

        Optional<ShiftReport> existing = shiftReportRepository
                .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(currentUser);

        if (existing.isPresent()) {
            throw new Exception("You already have an active shift. Please end it before starting a new one.");
        }
        Branch branch = currentUser.getBranch();

        ShiftReport shiftReport = ShiftReport.builder()
                .cashier(currentUser)
                .shiftStart(shiftStart)
                .branch(branch)
                .build();

        ShiftReport savedReport = shiftReportRepository.save(shiftReport);
        return ShiftReportMapper.toDTO(savedReport);
    }

    @Override
    public ShiftReportDTO endShift(Long shiftReportId, LocalDateTime shiftEnd) throws Exception {

        User currentUser = userService.getCurrentUser();
        ShiftReport shiftReport = shiftReportRepository
                .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(currentUser)
                .orElseThrow(() -> new Exception("Shift not found"));

        shiftReport.setShiftEnd(shiftEnd);

        List<Refund> refunds = refundRepository.findByCashierIdAndCreatedAtBetweenAndStatusIn(
                currentUser.getId(),
                shiftReport.getShiftStart(), shiftReport.getShiftEnd(),
                COUNTED_REFUND_STATUSES
        );
        for (Refund refund : refunds) {
            refund.setShiftReport(shiftReport);
        }

        double totalRefunds = refunds.stream()
                .mapToDouble(refund -> refund.getAmount() != null ?
                        refund.getAmount() : 0.0).sum();

        List<Order> orders = orderRepository.findByCashierAndCreatedAtBetween(
                currentUser,
                shiftReport.getShiftStart(), shiftReport.getShiftEnd()
        );
        double totalSales = orders.stream()
                .mapToDouble(Order::getTotalAmount).sum();

        int totalOrders = orders.size();
        double netSales = totalSales - totalRefunds;

        shiftReport.setTotalRefunds(totalRefunds);
        shiftReport.setTotalSale(totalSales);
        shiftReport.setTotalOrders(totalOrders);
        shiftReport.setNetSale(netSales);
        shiftReport.setRecentOrders(getRecentOrders(orders));
        shiftReport.setTopSellingProducts(getTopSellingProducts(orders));
        shiftReport.setPaymentSummaries(getPaymentSummaries(orders, totalSales));
        shiftReport.setRefunds(refunds);

        ShiftReport savedReport = shiftReportRepository.save(shiftReport);
        return ShiftReportMapper.toDTO(savedReport);
    }


    @Override
    public ShiftReportDTO getShiftReportById(Long id) throws Exception {
        return shiftReportRepository.findById(id)
                .map(ShiftReportMapper::toDTO).orElseThrow(
                        () -> new Exception("shift report not found with given id : " + id)
                );
    }

    @Override
    public List<ShiftReportDTO> getAllShiftReports() {
        List<ShiftReport> reports = shiftReportRepository.findAll();
        return reports.stream().map(
                ShiftReportMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public List<ShiftReportDTO> getShiftReportByBranchId(Long branchId) {
        List<ShiftReport> reports = shiftReportRepository.findByBranchId(branchId);
        return reports.stream().map(
                ShiftReportMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public List<ShiftReportDTO> getShiftReportByCashierId(Long cashierId) {
        List<ShiftReport> reports = shiftReportRepository.findByCashierId(cashierId);
        return reports.stream().map(
                ShiftReportMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShiftReportDTO getCurrentShiftProgress(Long cashierId) throws Exception {

        User user = userService.getCurrentUser();

        ShiftReport shiftReport = shiftReportRepository
                .findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(user)
                .orElseThrow(
                        () -> new Exception("no active shift found for cashier")
                );

        LocalDateTime now = LocalDateTime.now();
        List<Order> orders = orderRepository.findByCashierAndCreatedAtBetween(
                user, shiftReport.getShiftStart(), now
        );

        List<Refund> refunds = refundRepository.findByCashierIdAndCreatedAtBetweenAndStatusIn(
                user.getId(),
                shiftReport.getShiftStart(), now,
                COUNTED_REFUND_STATUSES
        );

        double totalRefunds = refunds.stream()
                .mapToDouble(refund -> refund.getAmount() != null ?
                        refund.getAmount() : 0.0).sum();

        double totalSales = orders.stream()
                .mapToDouble(Order::getTotalAmount).sum();

        int totalOrders = orders.size();
        double netSales = totalSales - totalRefunds;

        ShiftReport progressView = ShiftReport.builder()
                .id(shiftReport.getId())
                .cashier(shiftReport.getCashier())
                .branch(shiftReport.getBranch())
                .shiftStart(shiftReport.getShiftStart())
                .shiftEnd(shiftReport.getShiftEnd())
                .totalRefunds(totalRefunds)
                .totalSale(totalSales)
                .totalOrders(totalOrders)
                .netSale(netSales)
                .recentOrders(getRecentOrders(orders))
                .topSellingProducts(getTopSellingProducts(orders))
                .paymentSummaries(getPaymentSummaries(orders, totalSales))
                .refunds(refunds)
                .build();

        return ShiftReportMapper.toDTO(progressView);
    }

    @Override
    public ShiftReportDTO getShiftByCashierAndDate(Long cashierId, LocalDateTime date) throws Exception {
        User cashier = userRepository.findById(cashierId).orElseThrow(
                ()-> new Exception("cashier not found with given id : "+cashierId)
        );

        LocalDateTime start = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = date.withHour(23).withMinute(59).withSecond(59);

        ShiftReport report = shiftReportRepository.findByCashierAndShiftStartBetween(
                cashier,start,end
        ).orElseThrow(
                ()-> new Exception("shift report not found for date")
        );

        return ShiftReportMapper.toDTO(report);
    }



    //-----------------------------------Helper methods--------------------------------------



    private List<PaymentSummary> getPaymentSummaries(List<Order> orders, double totalSales) {

/*      CASH - order 1(amount = 1000), order2(amount = 1000) => 2000
        card - order 3 => 3000
        upi - order 4(amount=500), order 5(amount = 500) => 1000

        cash = 30%
        card = 50%
        upi = 20%*/

        Map<PaymentType, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getPaymentType() != null ?
                        order.getPaymentType() : PaymentType.CASH));

        List<PaymentSummary> summaries = new ArrayList<>();
        for (Map.Entry<PaymentType, List<Order>> entry : grouped.entrySet()) {
            double amount = entry.getValue().stream()
                    .mapToDouble(Order::getTotalAmount).sum();
            int transaction = entry.getValue().size();
            double percent = (amount / totalSales) * 100;

            PaymentSummary ps = new PaymentSummary();
            ps.setType(entry.getKey());
            ps.setTotalAmount(amount);
            ps.setTransactionCount(transaction);
            ps.setPercentage(percent);
            summaries.add(ps);
        }
        return summaries;
    }

    private List<Product> getTopSellingProducts(List<Order> orders) {
        Map<Product, Integer> productSalesMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                productSalesMap.put(product, productSalesMap.getOrDefault(product, 0) + item.getQuantity());
            }
        }

        return productSalesMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Order> getRecentOrders(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftReportDTO> getShiftReportsByStoreId(Long storeId) {
        List<ShiftReport> reports = shiftReportRepository.findByBranch_StoreId(storeId);
        return reports.stream().map(
                ShiftReportMapper::toDTO
        ).collect(Collectors.toList());
    }
}

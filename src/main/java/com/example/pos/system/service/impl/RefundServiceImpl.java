package com.example.pos.system.service.impl;

import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.RefundStatus;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.RefundMapper;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.RefundDTO;
import com.example.pos.system.repository.InventoryRepository;
import com.example.pos.system.repository.OrderRepository;
import com.example.pos.system.repository.PaymentRepository;
import com.example.pos.system.repository.RefundRepository;
import com.example.pos.system.service.NotificationService;
import com.example.pos.system.service.RefundService;
import com.example.pos.system.service.UserService;
import com.example.pos.system.repository.ShiftReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final UserService userService;
    private final RefundRepository refundRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    private final ShiftReportRepository shiftReportRepository;


    @Override
    public RefundDTO createRefund(RefundDTO refund) throws Exception {
        User cashier = userService.getCurrentUser();

        Order order = orderRepository.findById(refund.getOrderId()).orElseThrow(
                () -> new Exception("Order not found.")
        );
        Branch branch = order.getBranch();

        Refund createdRefund = Refund.builder()
                .order(order)
                .cashier(cashier)
                .branch(branch)
                .reason(refund.getReason())
                .amount(order.getTotalAmount())
                .createdAt(refund.getCreatedAt())
                .build();
        Refund savedRefund = refundRepository.save(createdRefund);
        return RefundMapper.toDTO(savedRefund);
    }

    @Override
    public List<RefundDTO> getAllRefunds() throws Exception {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ROLE_SUPER_ADMIN) {

            return refundRepository.findAll().stream()
                    .map(RefundMapper::toDTO)
                    .collect(Collectors.toList());

        } else if (currentUser.getRole() == UserRole.ROLE_STORE_ADMIN
                || currentUser.getRole() == UserRole.ROLE_ACCOUNTANT
                || currentUser.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {

            if (currentUser.getStore() == null) {
                return List.of();
            }

            return refundRepository.findByBranchStoreId(currentUser.getStore().getId()).stream()
                    .map(RefundMapper::toDTO)
                    .collect(Collectors.toList());

        } else if (currentUser.getBranch() != null) {

            return refundRepository.findByBranchId(currentUser.getBranch().getId()).stream()
                    .map(RefundMapper::toDTO)
                    .collect(Collectors.toList());

        } else {
            return List.of();
        }
    }

    @Override
    public List<RefundDTO> getRefundByCashier(Long cashierId) throws Exception {
        return refundRepository.findByCashierId(cashierId).stream().map(
                RefundMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> getRefundByShiftReport(Long shiftReport) throws Exception {
        return refundRepository.findByShiftReportId(shiftReport).stream().map(
                RefundMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> getRefundByCashierAndDateRange(Long cashierId,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) throws Exception {
        return refundRepository.findByCashierIdAndCreatedAtBetween(
                cashierId, startDate, endDate
        ).stream().map(RefundMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> getRefundByBranch(Long branchId) throws Exception {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != UserRole.ROLE_SUPER_ADMIN) {
            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId().equals(branchId)) {
                throw new UserException("You cannot access refunds for this branch");
            }
        }

        return refundRepository.findByBranchId(branchId).stream().map(
                RefundMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public RefundDTO getRefundById(Long refundId) throws Exception {
        return refundRepository.findById(refundId)
                .map(RefundMapper::toDTO).orElseThrow(
                        ()-> new Exception("Refund not found.")
                );
    }

    @Override
    public void deleteRefund(Long refundId) throws Exception {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new Exception("Refund not found."));

        if (refund.getStatus() == RefundStatus.APPROVED
                || refund.getStatus() == RefundStatus.PROCESSED) {
            throw new Exception("Cannot delete a refund that has already been approved or processed.");
        }

        refundRepository.deleteById(refundId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundDTO updateRefundStatus(Long id, RefundStatus status) throws Exception {

        User currentUser = userService.getCurrentUser();

        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new Exception("Refund not found"));


        if (refund.getStatus() == RefundStatus.APPROVED
                || refund.getStatus() == RefundStatus.PROCESSED) {

            throw new Exception("Refund already processed, cannot change status");
        }

        Order order = refund.getOrder();

        if (order.getStatus() == OrderStatus.RETURNED
                && (status == RefundStatus.APPROVED || status == RefundStatus.PROCESSED)) {

            throw new Exception("This order has already been returned/refunded. Cannot approve another refund for it.");
        }

        if (status == RefundStatus.APPROVED || status == RefundStatus.PROCESSED) {

            // 1. Inventory increase
            if (order.getItems() != null) {

                for (OrderItem item : order.getItems()) {

                    Inventory inventory = inventoryRepository
                            .findByProductIdAndBranchId(
                                    item.getProduct().getId(),
                                    order.getBranch().getId()
                            );

                    if (inventory != null) {

                        inventory.setQuantity(
                                inventory.getQuantity() + item.getQuantity()
                        );

                        Inventory savedInventory = inventoryRepository.save(inventory);
                        notificationService.checkAndNotifyLowStock(savedInventory);

                    }
                }
            }


            order.setStatus(OrderStatus.RETURNED);
            orderRepository.save(order);


            if (order.getPayment() != null) {

                order.getPayment().setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(order.getPayment());

            }


            refund.setApprovedBy(currentUser);
            refund.setApprovedAt(LocalDateTime.now());

            ShiftReport shiftReport = shiftReportRepository
                    .findByCashierAndShiftStartLessThanEqualAndShiftEndGreaterThanEqual(
                            order.getCashier(),
                            order.getCreatedAt(),
                            order.getCreatedAt()
                    )
                    .orElse(null);

            if (shiftReport != null) {

                refund.setShiftReport(shiftReport);

                List<Refund> shiftRefunds = refundRepository
                        .findByShiftReportIdAndStatusIn(
                                shiftReport.getId(),
                                List.of(RefundStatus.APPROVED, RefundStatus.PROCESSED)
                        );

                shiftRefunds.add(refund);

                double totalRefunds = shiftRefunds.stream()
                        .mapToDouble(r -> r.getAmount() != null ? r.getAmount() : 0.0)
                        .sum();

                shiftReport.setTotalRefunds(totalRefunds);
                shiftReport.setNetSale(shiftReport.getTotalSale() - totalRefunds);
                shiftReport.setRefunds(shiftRefunds);

                shiftReportRepository.save(shiftReport);
            }

        }

        refund.setStatus(status);

        Refund updated = refundRepository.save(refund);

        return RefundMapper.toDTO(updated);
    }
}

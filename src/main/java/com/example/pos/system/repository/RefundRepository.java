package com.example.pos.system.repository;

import com.example.pos.system.domain.RefundStatus;
import com.example.pos.system.modal.Refund;
import com.example.pos.system.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findByCashierIdAndCreatedAtBetween(
            Long cashier,
            LocalDateTime from,
            LocalDateTime to
    );
    List<Refund> findByCashierId(Long id);
    List<Refund> findByShiftReportId(Long id);
    List<Refund> findByBranchId(Long id);

    @Query("""
        SELECT r FROM Refund r
        LEFT JOIN FETCH r.order
        LEFT JOIN FETCH r.cashier
        LEFT JOIN FETCH r.branch
        ORDER BY r.createdAt DESC
        """)
    List<Refund> findRecentRefunds(Pageable pageable);

    List<Refund> findByCashierIdAndCreatedAtBetweenAndStatusIn(
            Long cashierId,
            LocalDateTime start,
            LocalDateTime end,
            List<RefundStatus> statuses
    );

    List<Refund> findByShiftReportIdAndStatusIn(
            Long shiftReportId,
            List<RefundStatus> statuses
    );
    List<Refund> findByBranchStoreId(Long storeId);
}

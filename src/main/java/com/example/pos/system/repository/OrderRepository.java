package com.example.pos.system.repository;
import org.springframework.data.jpa.repository.Query;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pos.system.domain.PaymentType;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByBranchId(Long branchId);
    List<Order> findByCashierId(Long cashierId);
    List<Order> findByBranchIdAndCreatedAtBetween(Long branchId, LocalDateTime from, LocalDateTime to);
    List<Order> findByCashierAndCreatedAtBetween(User cashier, LocalDateTime from, LocalDateTime to);
    List<Order> findTop5ByBranchIdOrderByCreatedAtDesc(Long branchId);

    @Query("""
SELECT o.paymentType,
SUM(o.totalAmount),
COUNT(o)
FROM Order o
WHERE o.branch.id = :branchId
GROUP BY o.paymentType
""")
    List<Object[]> getPaymentSummary(Long branchId);
    @Query("""
    SELECT o FROM Order o
    LEFT JOIN FETCH o.branch
    LEFT JOIN FETCH o.cashier
    LEFT JOIN FETCH o.customer
    LEFT JOIN FETCH o.items i
    LEFT JOIN FETCH i.product
    WHERE o.id = :id
""")
    Optional<Order> findOrderWithDetails(
            @Param("id") Long id
    );
    @Query("""
SELECT COALESCE(SUM(o.totalAmount), 0)
FROM Order o
WHERE o.createdAt BETWEEN :start AND :end
""")
    Double getTodayRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
SELECT COUNT(o)
FROM Order o
WHERE o.createdAt BETWEEN :start AND :end
""")
    Long getTodayOrders(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
SELECT COALESCE(SUM(o.totalAmount),0)
FROM Order o
""")
    Double getTotalRevenue();

    @Query("""
SELECT COUNT(o)
FROM Order o
""")
    Long getTotalOrders();

    List<Order> findTop5ByOrderByCreatedAtDesc();


    @Query("""
SELECT o.paymentType, COUNT(o)
FROM Order o
GROUP BY o.paymentType
""")
    List<Object[]> getPaymentTypeBreakdown();

    List<Order> findByBranch_Store_Id(Long storeId);
}

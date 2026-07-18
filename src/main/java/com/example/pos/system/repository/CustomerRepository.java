package com.example.pos.system.repository;

import com.example.pos.system.modal.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String email
    );

    List<Customer> findByStoreId(Long storeId);

    @Query("""
            SELECT c
            FROM Customer c
            WHERE c.store.id = :storeId
            AND (
                LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    List<Customer> searchByStore(
            @Param("storeId") Long storeId,
            @Param("keyword") String keyword
    );
    @Query("""
SELECT COUNT(c)
FROM Customer c
WHERE c.createdAt BETWEEN :start AND :end
""")
    Long getTodayCustomers(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    long countByBranchId(Long branchId);
}
package com.example.pos.system.repository;


import com.example.pos.system.modal.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PaymentRepository
        extends JpaRepository<Payment,Long> {


    Payment findByOrderId(Long orderId);

    @Query("SELECT p.paymentType, COUNT(p) FROM Payment p GROUP BY p.paymentType")
    List<Object[]> countGroupByPaymentType();

    List<Payment> findByOrder_Branch_Id(Long branchId);

    List<Payment> findByOrder_Branch_Store_Id(Long storeId);

}
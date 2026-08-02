package com.example.pos.system.service;


import com.example.pos.system.payload.dto.PaymentDTO;

import java.util.List;


public interface PaymentService {


    PaymentDTO createPayment(
            PaymentDTO paymentDTO
    ) throws Exception;



    PaymentDTO getPaymentByOrderId(
            Long orderId
    ) throws Exception;

    List<PaymentDTO> getPaymentsByBranch(Long branchId) throws Exception;

    List<PaymentDTO> getAllPayments()
            throws Exception;



    void deletePayment(Long id)
            throws Exception;

}
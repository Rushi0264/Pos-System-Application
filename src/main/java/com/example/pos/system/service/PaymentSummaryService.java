package com.example.pos.system.service;

import com.example.pos.system.modal.PaymentSummary;

import java.util.List;

public interface PaymentSummaryService {

    List<PaymentSummary> getPaymentSummary(Long branchId) throws Exception;

}
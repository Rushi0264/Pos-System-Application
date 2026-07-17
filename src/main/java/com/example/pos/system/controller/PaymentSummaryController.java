package com.example.pos.system.controller;

import com.example.pos.system.modal.PaymentSummary;
import com.example.pos.system.service.PaymentSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-summary")
public class PaymentSummaryController {

    private final PaymentSummaryService paymentSummaryService;

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<PaymentSummary>> getSummary(
            @PathVariable Long branchId
    ) throws Exception {

        return ResponseEntity.ok(
                paymentSummaryService.getPaymentSummary(branchId)
        );
    }
}
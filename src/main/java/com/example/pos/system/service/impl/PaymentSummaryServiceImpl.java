package com.example.pos.system.service.impl;

import com.example.pos.system.modal.PaymentSummary;
import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.repository.OrderRepository;
import com.example.pos.system.service.PaymentSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentSummaryServiceImpl
        implements PaymentSummaryService {

    private final OrderRepository orderRepository;

    @Override
    public List<PaymentSummary> getPaymentSummary(Long branchId)
            throws Exception {

        List<Object[]> result =
                orderRepository.getPaymentSummary(branchId);

        double grandTotal = result.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();

        List<PaymentSummary> list = new ArrayList<>();

        for (Object[] row : result) {

            PaymentType type = (PaymentType) row[0];

            double amount =
                    ((Number) row[1]).doubleValue();

            int count =
                    ((Number) row[2]).intValue();

            double percentage =
                    grandTotal == 0
                            ? 0
                            : (amount * 100) / grandTotal;

            list.add(
                    PaymentSummary.builder()
                            .type(type)
                            .totalAmount(amount)
                            .transactionCount(count)
                            .percentage(percentage)
                            .build()
            );
        }

        return list;
    }
}
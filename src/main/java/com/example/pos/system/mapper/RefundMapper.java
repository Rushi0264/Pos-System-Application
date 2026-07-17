package com.example.pos.system.mapper;

import com.example.pos.system.modal.Refund;
import com.example.pos.system.payload.dto.RefundDTO;

public class RefundMapper {

    public static RefundDTO toDTO(Refund refund){
        return RefundDTO.builder()
                .id(refund.getId())
                .order(OrderMapper.toDTO(refund.getOrder()))
                .orderId(refund.getOrder().getId())
                .reason(refund.getReason())
                .amount(refund.getAmount())
                .cashierName(refund.getCashier().getFullName())
                .branchId(refund.getBranch().getId())
                .shiftReportId(refund.getShiftReport()!=null? refund.getShiftReport().getId():null)
                .status(refund.getStatus())
                .approvedByName(refund.getApprovedBy()!=null ? refund.getApprovedBy().getFullName() : null)
                .approvedAt(refund.getApprovedAt())
                .createdAt(refund.getCreatedAt())
                .build();
    }
}
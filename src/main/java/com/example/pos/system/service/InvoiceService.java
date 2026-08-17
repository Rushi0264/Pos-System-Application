package com.example.pos.system.service;


public interface InvoiceService {

    byte[] generateInvoice(Long orderId) throws Exception;

    byte[] generateRefundReceipt(Long refundId) throws Exception;
}
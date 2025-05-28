package com.aidas.payments.service;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Long createPayment(PaymentRequest request, String clientIp);

    PaymentResponse cancelPayment(Long id);

    List<Payment> getAllActivePayments();

    List<Payment> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);

    PaymentResponse getPaymentById(Long id);
}

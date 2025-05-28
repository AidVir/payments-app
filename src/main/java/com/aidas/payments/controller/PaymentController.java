package com.aidas.payments.controller;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> createPayment(@Valid @RequestBody PaymentRequest request, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        Long id = paymentService.createPayment(request, ip);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllActivePayments() {
        return ResponseEntity.ok(paymentService.getAllActivePayments());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Payment>> getPaymentsByAmountRange(
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {
        return ResponseEntity.ok(paymentService.getPaymentsByAmountRange(minAmount, maxAmount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
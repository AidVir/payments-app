package com.aidas.payments.service;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.exception.BusinessException;
import com.aidas.payments.exception.NotFoundException;
import com.aidas.payments.handler.PaymentHandler;
import com.aidas.payments.handler.PaymentHandlerFactory;
import com.aidas.payments.mapper.PaymentMapper;
import com.aidas.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentHandlerFactory handlerFactory;
    private final AsyncTaskService asyncTaskService;
    private final PaymentMapper mapper;

    @Transactional
    public Long createPayment(PaymentRequest request, String clientIp) {
        PaymentHandler handler = handlerFactory.getHandler(request.getType());
        handler.validate(request);

        Payment payment = mapper.toEntity(request);

        handler.addTypeSpecificProperties(payment, request);
        Payment savedPayment = repository.save(payment);

        asyncTaskService.resolveCountry(clientIp, savedPayment.getId());
        handler.getNotificationUrl().ifPresent(url -> asyncTaskService.notify(url, savedPayment.getId()));

        return savedPayment.getId();
    }

    @Transactional
    public PaymentResponse cancelPayment(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        LocalDateTime currentTime = LocalDateTime.now();

        if (payment.isCancelled() || !(currentTime.toLocalDate().equals(payment.getCreatedAt().toLocalDate()))) {
            throw new BusinessException("Payment cannot be cancelled");
        }

        PaymentHandler handler = handlerFactory.getHandler(payment.getType());
        BigDecimal fee = handler.calculateCancellationFee(payment);


        Payment updated = payment.toBuilder()
                .cancelled(true)
                .cancelledAt(LocalDateTime.now())
                .cancellationFee(fee)
                .build();

        repository.save(updated);

        return PaymentResponse.builder()
                .id(updated.getId())
                .cancellationFee(fee)
                .build();
    }

    public List<Payment> getAllActivePayments() {
        return repository.findByCancelledFalse();
    }

    public List<Payment> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return repository.findByAmountRange(minAmount, maxAmount);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        return PaymentResponse.builder()
                .id(payment.getId())
                .cancellationFee(payment.getCancellationFee())
                .build();
    }
}

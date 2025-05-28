package com.aidas.payments.service;

import com.aidas.payments.dto.NotificationResponse;
import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.NotificationLog;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHandlerFactory handlerFactory;
    private final AsyncTaskService asyncTaskService;
    private final PaymentMapper mapper;
    private final NotificationLogService notificationLogService;

    @Transactional
    public Long createPayment(PaymentRequest request, String clientIp) {
        PaymentHandler handler = handlerFactory.getHandler(request.getType());
        handler.validate(request);

        Payment payment = mapper.toEntity(request);

        handler.addTypeSpecificProperties(payment, request);
        Payment savedPayment = paymentRepository.save(payment);

        asyncTaskService.resolveCountry(clientIp, savedPayment.getId());
        handler.getNotificationUrl().ifPresent(url -> asyncTaskService.notify(url, savedPayment.getId()));

        return savedPayment.getId();
    }

    @Transactional
    public PaymentResponse cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
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

        paymentRepository.save(updated);

        return PaymentResponse.builder()
                .id(updated.getId())
                .cancellationFee(fee)
                .build();
    }

    public List<Payment> getAllActivePayments() {
        return paymentRepository.findByCancelledFalse();
    }

    public List<Payment> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findByAmountRange(minAmount, maxAmount);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        return PaymentResponse.builder()
                .id(payment.getId())
                .cancellationFee(payment.getCancellationFee())
                .build();
    }

    public NotificationResponse getNotificationByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        NotificationLog log = Optional.ofNullable(payment.getNotificationLog())
                .orElseThrow(() -> new NotFoundException("No notification log for this payment"));

        return NotificationResponse.builder()
                .paymentId(paymentId)
                .success(log.isSuccess())
                .targetService(log.getTargetService())
                .timestamp(log.getTimestamp())
                .build();
    }
}

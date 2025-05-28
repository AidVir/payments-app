package com.aidas.payments.handler;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.aidas.payments.util.CurrencyUtil.convertToEur;

public interface PaymentHandler {
    void validate(PaymentRequest request);

    void addTypeSpecificProperties(Payment payment, PaymentRequest request);

    default BigDecimal calculateCancellationFee(Payment payment) {
        long hours = Duration.between(payment.getCreatedAt(), LocalDateTime.now()).toHours();
        BigDecimal rawFee = BigDecimal.valueOf(hours).multiply(getCancellationCoefficient());

        return convertToEur(rawFee, payment.getCurrency());
    }

    Optional<String> getNotificationUrl();

    BigDecimal getCancellationCoefficient();

    PaymentType getType();
}


package com.aidas.payments.handler;

import com.aidas.payments.config.NotificationProperties;
import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Type1PaymentHandler implements PaymentHandler {

    private final NotificationProperties notificationProperties;

    public void validate(PaymentRequest request) {
        if (request.getCurrency() != Currency.EUR)
            throw new BusinessException("TYPE1 supports only EUR");

        if (request.getDetails() == null || request.getDetails().isBlank()) {
            throw new BusinessException("TYPE1 payments must include a details field");
        }

        if (request.getCreditorBankBic() != null) {
            throw new BusinessException("TYPE1 payments must not include creditorBankBic");
        }
    }

    public void addTypeSpecificProperties(Payment payment, PaymentRequest request) {
        payment.setDetails(request.getDetails());
    }

    public Optional<String> getNotificationUrl() {
        return Optional.of(notificationProperties.getType1Url());
    }

    public BigDecimal getCancellationCoefficient() {
        return BigDecimal.valueOf(0.05);
    }

    public PaymentType getType() {
        return PaymentType.TYPE1;
    }
}
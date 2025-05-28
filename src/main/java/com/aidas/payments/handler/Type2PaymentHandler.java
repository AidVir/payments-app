package com.aidas.payments.handler;

import com.aidas.payments.config.NotificationProperties;
import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Type2PaymentHandler implements PaymentHandler {

    private final NotificationProperties notificationProperties;

    public void validate(PaymentRequest request) {
        if (request.getCurrency() != Currency.USD) {
            throw new BusinessException("TYPE2 supports only USD");
        }

        if (request.getCreditorBankBic() != null) {
            throw new BusinessException("TYPE2 payments must not include creditorBankBic");
        }
    }

    public void addTypeSpecificProperties(Payment payment, PaymentRequest request) {
        Optional.ofNullable(request.getDetails())
                .filter(StringUtils::hasText)
                .ifPresent(payment::setDetails);
    }

    public Optional<String> getNotificationUrl() {
        return Optional.of(notificationProperties.getType2Url());
    }

    public BigDecimal getCancellationCoefficient() {
        return BigDecimal.valueOf(0.10);
    }

    public PaymentType getType() {
        return PaymentType.TYPE2;
    }
}


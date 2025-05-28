package com.aidas.payments.handler;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class Type3PaymentHandler implements PaymentHandler {

    public void validate(PaymentRequest request) {
        if (request.getCurrency() != Currency.EUR && request.getCurrency() != Currency.USD) {
            throw new BusinessException("TYPE3 supports only EUR or USD");
        }
        
        if (request.getCreditorBankBic() == null || request.getCreditorBankBic().isBlank()) {
            throw new BusinessException("TYPE3 requires 'creditorBankBic'");
        }

        if (request.getDetails() != null) {
            throw new BusinessException("TYPE3 payments must not include details");
        }
    }

    public void addTypeSpecificProperties(Payment payment, PaymentRequest request) {
        payment.setCreditorBankBic(request.getCreditorBankBic());
    }

    public Optional<String> getNotificationUrl() {
        return Optional.empty();
    }

    public BigDecimal getCancellationCoefficient() {
        return BigDecimal.valueOf(0.15);
    }

    public PaymentType getType() {
        return PaymentType.TYPE3;
    }
}


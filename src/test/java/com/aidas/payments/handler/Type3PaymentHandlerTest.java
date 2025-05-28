package com.aidas.payments.handler;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Type3PaymentHandlerTest {

    private final Type3PaymentHandler handler = new Type3PaymentHandler();

    @Test
    void shouldThrowIfCurrencyIsNotEURorUSD() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE3)
                .currency(null)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .creditorBankBic("BIC")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("EUR or USD");
    }

    @Test
    void shouldThrowIfCreditorBicMissing() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE3)
                .currency(Currency.EUR)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("creditorBankBic");
    }

    @Test
    void shouldThrowIfDetailsPresent() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE3)
                .currency(Currency.USD)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .creditorBankBic("BIC")
                .details("not allowed")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must not include details");
    }
}

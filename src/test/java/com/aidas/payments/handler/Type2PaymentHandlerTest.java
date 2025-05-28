package com.aidas.payments.handler;

import com.aidas.payments.config.NotificationProperties;
import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Type2PaymentHandlerTest {

    private final NotificationProperties props = new NotificationProperties();
    private final Type2PaymentHandler handler = new Type2PaymentHandler(props);

    @Test
    void shouldThrowIfCurrencyIsNotUSD() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE2)
                .currency(Currency.EUR)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("USD");
    }

    @Test
    void shouldThrowIfCreditorBankBicProvided() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE2)
                .currency(Currency.USD)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .creditorBankBic("SOMEBIC")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not include creditorBankBic");
    }

    @Test
    void shouldPassWithOptionalDetailsOnly() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE2)
                .currency(Currency.USD)
                .amount(BigDecimal.ONE)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .details("optional info")
                .build();

        assertThatCode(() -> handler.validate(request)).doesNotThrowAnyException();
    }
}

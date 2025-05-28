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

class Type1PaymentHandlerTest {

    private final NotificationProperties props = new NotificationProperties();
    private final Type1PaymentHandler handler = new Type1PaymentHandler(props);

    @Test
    void shouldThrowIfCurrencyIsNotEUR() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE1)
                .currency(Currency.USD)
                .amount(BigDecimal.TEN)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .details("info")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("EUR");
    }

    @Test
    void shouldThrowIfDetailsMissing() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE1)
                .currency(Currency.EUR)
                .amount(BigDecimal.TEN)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .build();

        assertThatThrownBy(() -> handler.validate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("details");
    }

    @Test
    void shouldPassWithValidType1Data() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE1)
                .currency(Currency.EUR)
                .amount(BigDecimal.TEN)
                .debtorIban("LT111111111111111111")
                .creditorIban("LT222222222222222222")
                .details("valid")
                .build();

        assertThatCode(() -> handler.validate(request)).doesNotThrowAnyException();
    }
}


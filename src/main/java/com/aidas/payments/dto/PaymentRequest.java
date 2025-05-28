package com.aidas.payments.dto;

import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class PaymentRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotBlank
    private String debtorIban;

    @NotBlank
    private String creditorIban;

    @NotNull
    private PaymentType type;

    private String details;
    private String creditorBankBic;
}

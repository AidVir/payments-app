package com.aidas.payments.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private BigDecimal cancellationFee;
}
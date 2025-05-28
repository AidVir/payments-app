package com.aidas.payments.entity;

import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String debtorIban;
    private String creditorIban;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private String details;
    private String creditorBankBic;

    private boolean cancelled;
    private BigDecimal cancellationFee;

    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    @OneToOne(cascade = CascadeType.ALL)
    private NotificationLog notificationLog;
}
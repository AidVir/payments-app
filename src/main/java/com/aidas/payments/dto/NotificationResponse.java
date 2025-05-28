package com.aidas.payments.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long paymentId;
    private boolean success;
    private String targetService;
    private LocalDateTime timestamp;
}

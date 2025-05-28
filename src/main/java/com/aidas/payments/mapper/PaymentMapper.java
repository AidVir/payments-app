package com.aidas.payments.mapper;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.Payment;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancelled", constant = "false")
    @Mapping(target = "cancellationFee", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "notificationLog", ignore = true)
    Payment toEntity(PaymentRequest request);

    PaymentResponse toResponse(Payment payment);
}

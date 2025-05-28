package com.aidas.payments.handler;

import com.aidas.payments.enums.PaymentType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentHandlerFactory {

    private final Map<PaymentType, PaymentHandler> handlers;

    public PaymentHandlerFactory(List<PaymentHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(PaymentHandler::getType, Function.identity()));
    }

    public PaymentHandler getHandler(PaymentType type) {
        return Optional.ofNullable(handlers.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment type: " + type));
    }
}

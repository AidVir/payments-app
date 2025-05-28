package com.aidas.payments.util;

import com.aidas.payments.enums.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtil {

    private static final BigDecimal USD_TO_EUR = new BigDecimal("0.93");

    public static BigDecimal convertToEur(BigDecimal amount, Currency currency) {
        if (currency == Currency.EUR) {
            return amount;
        } else if (currency == Currency.USD) {
            return amount.multiply(USD_TO_EUR).setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }
}
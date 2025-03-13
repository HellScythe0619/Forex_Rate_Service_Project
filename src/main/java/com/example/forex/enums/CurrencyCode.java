package com.example.forex.enums;

import java.util.Arrays;

public enum CurrencyCode {
    USD("usd"),
    ;

    private final String code;

    CurrencyCode(String code) {
        this.code = code;
    }
    
    public static CurrencyCode getCurrencyCode(String code) {
        return Arrays.stream(CurrencyCode.values())
            .filter(currencyCode -> currencyCode.code.equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }
}

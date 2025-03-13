package com.example.forex.model;

import lombok.Data;
import java.util.List;

@Data
public class ApiResponse {
    private Error error;
    private List<CurrencyData> currency;

    @Data
    public static class Error {
        private String code;
        private String message;
    }

    @Data
    public static class CurrencyData {
        private String date;
        private String usd;
    }
} 
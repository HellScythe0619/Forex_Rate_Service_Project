package com.example.forex.enums;

public enum ResponseCode {
    SUCCESS("0000", "成功"),
    INVALID_DATE_RANGE("E001", "日期區間不符"),
    INVALID_CURRENCY("E002", "不支持的幣別"),
    ERROR("E999", "系統錯誤"),
    ;

    private String code;
    private String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

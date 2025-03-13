package com.example.forex.service;

import com.example.forex.enums.CurrencyCode;
import com.example.forex.enums.ResponseCode;
import com.example.forex.model.ApiResponse;
import com.example.forex.model.ForexRate;
import com.example.forex.repository.ForexRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForexService {
    
    @Autowired
    private ForexRateRepository repository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * 查詢匯率
     * @param startDate
     * @param endDate
     * @param currency
     * @return
     */
    public ResponseEntity<?> getForexRates(String startDate, String endDate, String currency) {
        try {
            // 驗證幣別
            CurrencyCode currencyCode = CurrencyCode.getCurrencyCode(currency);
            if (currencyCode == null) {
                return createResponseEntity(createErrorResponse(ResponseCode.INVALID_CURRENCY));
            }

            // 解析日期
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // 驗證日期範圍
            if (start.isBefore(oneYearAgo) || end.isAfter(yesterday) || start.isAfter(end)) {
                return createResponseEntity(createErrorResponse(ResponseCode.INVALID_DATE_RANGE));
            }

            // 查詢數據
            List<ForexRate> rates = repository.findByDateBetweenOrderByDateAsc(
                    start.atStartOfDay(),
                    end.atTime(23, 59, 59));

            // 構建響應
            return createResponseEntity(createSuccessResponse(rates, currencyCode));
        } catch (Exception e) {
            return createResponseEntity(createErrorResponse(ResponseCode.ERROR));
        }
    }
    
    /**
     * 構建錯誤響應
     * @param responseCode
     * @return
     */
    private ApiResponse createErrorResponse(ResponseCode responseCode) {
        ApiResponse response = new ApiResponse();
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode(responseCode.getCode());
        error.setMessage(responseCode.getMessage());
        response.setError(error);
        return response;
    }
    
    /**
     * 構建成功響應
     * @param rates
     * @param currencyCode
     * @return
     */
    private ApiResponse createSuccessResponse(List<ForexRate> rates, CurrencyCode currencyCode) {
        ApiResponse response = new ApiResponse();
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode(ResponseCode.SUCCESS.getCode());
        error.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setError(error);

        List<ApiResponse.CurrencyData> currencyList = new ArrayList<>();
        for (ForexRate rate : rates) {
            ApiResponse.CurrencyData data = new ApiResponse.CurrencyData();
            data.setDate(rate.getDate().toLocalDate().format(OUTPUT_FORMATTER));
            if (currencyCode == CurrencyCode.USD) {
                data.setUsd(String.format("%.3f", rate.getUsd()));
            }
            currencyList.add(data);
        }
        response.setCurrency(currencyList);

        return response;
    }
    
    /**
     * 將 ApiResponse 封裝成 ResponseEntity
     * @param response
     * @return
     */
    private ResponseEntity<?> createResponseEntity(ApiResponse response) {
        return ResponseEntity.ok(response);
    }
} 
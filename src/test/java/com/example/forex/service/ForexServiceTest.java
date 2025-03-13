package com.example.forex.service;

import com.example.forex.model.ApiResponse;
import com.example.forex.model.ForexRate;
import com.example.forex.repository.ForexRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ForexServiceTest {

    @InjectMocks
    private ForexService forexService;

    @Mock
    private ForexRateRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getForexRates_Success() {
        // 準備測試數據
        ForexRate rate = new ForexRate();
        rate.setDate(LocalDateTime.now().minusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0));
        rate.setUsd(31.01);

        when(repository.findByDateBetweenOrderByDateAsc(any(), any()))
            .thenReturn(Arrays.asList(rate));

        // 執行測試
        String yesterday = LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        ResponseEntity<?> response = forexService.getForexRates(
            yesterday,
            yesterday,
            "usd"
        );

        // 驗證結果
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("0000", apiResponse.getError().getCode());
        assertEquals("成功", apiResponse.getError().getMessage());
        
        List<ApiResponse.CurrencyData> currencyList = apiResponse.getCurrency();
        assertNotNull(currencyList);
        assertEquals(1, currencyList.size());
        assertEquals(rate.getDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")), 
                    currencyList.get(0).getDate());
        assertEquals(String.format("%.3f", rate.getUsd()), currencyList.get(0).getUsd());
    }

    @Test
    void getForexRates_InvalidDateRange() {
        // 執行測試 - 日期範圍超過一年
        ResponseEntity<?> response = forexService.getForexRates(
            "2022/01/01",
            "2024/01/01",
            "usd"
        );

        // 驗證結果
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("E001", apiResponse.getError().getCode());
        assertEquals("日期區間不符", apiResponse.getError().getMessage());
    }

    @Test
    void getForexRates_InvalidCurrency() {
        // 執行測試 - 不支持的幣別
        ResponseEntity<?> response = forexService.getForexRates(
            "2024/01/01",
            "2024/01/01",
            "eur"
        );

        // 驗證結果
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("E002", apiResponse.getError().getCode());
        assertEquals("不支持的幣別", apiResponse.getError().getMessage());
    }

    @Test
    void getForexRates_InvalidDateFormat() {
        // 執行測試 - 錯誤的日期格式
        ResponseEntity<?> response = forexService.getForexRates(
            "2024-01-01", // 錯誤的格式
            "2024/01/01",
            "usd"
        );

        // 驗證結果
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("E999", apiResponse.getError().getCode());
        assertEquals("系統錯誤", apiResponse.getError().getMessage());
    }
} 
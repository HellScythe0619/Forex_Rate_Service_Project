package com.example.forex.service;

import com.example.forex.model.ForexRate;
import com.example.forex.model.TaifexResponse;
import com.example.forex.repository.ForexRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ForexScheduleServiceTest {

    @InjectMocks
    private ForexScheduleService scheduleService;

    @Mock
    private ForexRateRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchDailyForexRates_Success() {
        // 準備測試數據
        TaifexResponse response = new TaifexResponse();
        response.setDate("20250312");
        response.setUSD("32.962");
        
        when(restTemplate.getForObject(anyString(), eq(TaifexResponse[].class)))
            .thenReturn(new TaifexResponse[]{response});
            
        when(repository.findAll()).thenReturn(new ArrayList<>());

        // 執行測試
        scheduleService.fetchDailyForexRates();

        // 驗證保存的數據
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ForexRate>> forexRatesCaptor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(forexRatesCaptor.capture());
        
        List<ForexRate> savedRates = forexRatesCaptor.getValue();
        assertEquals(1, savedRates.size());
        ForexRate savedRate = savedRates.get(0);
        assertEquals(LocalDateTime.of(2025, 3, 12, 18, 0), savedRate.getDate());
        assertEquals(32.962, savedRate.getUsd());
    }

    @Test
    void fetchDailyForexRates_EmptyResponse() {
        // 模擬空響應
        when(restTemplate.getForObject(anyString(), eq(TaifexResponse[].class)))
            .thenReturn(new TaifexResponse[]{});

        // 執行測試
        scheduleService.fetchDailyForexRates();

        // 驗證結果 - 不應該保存數據
        verify(repository, never()).save(any(ForexRate.class));
    }

    @Test
    void fetchDailyForexRates_ApiError() {
        // 模擬API錯誤
        when(restTemplate.getForObject(anyString(), eq(TaifexResponse[].class)))
            .thenThrow(new RuntimeException("API Error"));

        // 執行測試
        scheduleService.fetchDailyForexRates();

        // 驗證結果 - 確保不會拋出異常且不保存數據
        verify(repository, never()).save(any(ForexRate.class));
    }

    @Test
    void fetchDailyForexRates_InvalidData() {
        // 準備無效的測試數據
        TaifexResponse response = new TaifexResponse();
        response.setDate("invalid-date");
        response.setUSD("not-a-number");
        
        when(restTemplate.getForObject(anyString(), eq(TaifexResponse[].class)))
            .thenReturn(new TaifexResponse[]{response});

        // 執行測試
        scheduleService.fetchDailyForexRates();

        // 驗證結果 - 不應該保存數據
        verify(repository, never()).save(any(ForexRate.class));
    }
} 
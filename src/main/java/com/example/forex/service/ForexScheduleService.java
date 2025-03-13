package com.example.forex.service;

import com.example.forex.model.ForexRate;
import com.example.forex.model.TaifexResponse;
import com.example.forex.repository.ForexRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForexScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ForexScheduleService.class);

    @Autowired
    private ForexRateRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String API_TAIFEX_URL = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";

    /**
     * 每天下午 6 點調用匯率 API
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void fetchDailyForexRates() {
        try {
            logger.info("開始調用匯率 API: {}", API_TAIFEX_URL);
            TaifexResponse[] responses = restTemplate.getForObject(API_TAIFEX_URL, TaifexResponse[].class);
            logger.info("API 響應數量: {}", responses != null ? responses.length : 0);

            if (responses != null && responses.length > 0) {
                List<String> forexRateDateInDB = repository.findAll().stream()
                        .map(forexRate -> forexRate.getDate().format(API_DATE_FORMATTER))
                        .collect(Collectors.toList());

                List<ForexRate> forexRateList = new ArrayList<>();

                // 排除DB既有的匯率日期資料
                Arrays.stream(responses)
                    .filter(response -> !forexRateDateInDB.contains(response.getDate()))
                    .forEach(response -> {
                        logger.info("處理匯率數據: Date={}, USD/NTD={}", response.getDate(), response.getUSD());
                        ForexRate forexRate = new ForexRate();
                        LocalDate date = LocalDate.parse(response.getDate(), API_DATE_FORMATTER);
                        forexRate.setDate(date.atTime(18, 0));
                        forexRate.setUsd(Double.parseDouble(response.getUSD()));
                        forexRateList.add(forexRate);
                    });

                if (!forexRateList.isEmpty()) {
                    repository.saveAll(forexRateList);
                    logger.info("成功保存 {} 筆匯率數據", forexRateList.size());
                }
            } else {
                logger.warn("API 未返回數據");
            }
        } catch (Exception e) {
            logger.error("獲取匯率數據時發生錯誤", e);
        }
    }
}
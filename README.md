# 外匯匯率服務 (Forex Rate Service)

這是一個基於 Java Spring Boot 的外匯匯率服務專案，用於獲取和儲存美元/台幣 (USD/NTD) 的匯率數據。

## 功能敘述

1. 自動獲取匯率數據
   - 每日 18:00 從台灣期貨交易所 API 取得外匯成交資料
   - 自動過濾並只保存新的匯率數據
   - API 來源：[台灣期貨交易所](https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates)

2. 歷史匯率查詢
   - 支持查詢特定日期範圍內的匯率數據
   - 日期範圍限制：一年內且不超過昨天
   - 目前支持幣別：USD/NTD

## Technical Architecture

- Java 11
- Spring Boot 2.7.0
- MongoDB
- Maven

## System Requirement

- JDK 11 或以上
- MongoDB 4.0 或以上
- Maven 3.6 或以上

## STEP

1. Ensure MongoDB service 已啟動
bash
mongod

2. Compile & Run project

```bash
mvn clean install
mvn spring-boot:run
```

3. Test API
```bash
curl -X POST http://localhost:8080/api/forex \
-H "Content-Type: application/json" \
-d '{
    "startDate": "2024/01/01",
    "endDate": "2024/01/10",
    "currency": "usd"
}'
```

## API 文檔

### 查詢歷史匯率

- **URL**: `/api/forex`
- **Method**: POST
- **Request Body**:
```json
{
    "startDate": "2024/01/01",
    "endDate": "2024/01/10",
    "currency": "usd"
}
```
- **Success Response**:
```json
{
    "error": {
        "code": "0000",
        "message": "成功"
    },
    "currency": [
        {
            "date": "20240101",
            "usd": "31.010"
        }
    ]
}
```
- **Error Response**:
```json
{
    "error": {
        "code": "E001",
        "message": "日期區間不符"
    }
}
```

### 錯誤代碼說明

- `0000`: 成功
- `E001`: 日期區間不符
- `E002`: 不支持的幣別
- `E999`: 系統錯誤

## 配置說明

配置文件位於 `src/main/resources/application.yml`：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/forex_db

server:
  port: 8080

logging:
  level:
    com.example.forex: DEBUG
    org.springframework.data.mongodb: DEBUG
```

## Unit Test

運行所有測試：
```bash
mvn test
```

package com.example.forex.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@Document(collection = "forex_rates")
public class ForexRate {
    @Id
    private String id;
    
    @Indexed
    private LocalDateTime date;
    
    private Double usd;
} 
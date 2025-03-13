package com.example.forex.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class TaifexResponse {
    @JsonProperty("Date")
    private String Date;
    
    @JsonProperty("USD/NTD")
    private String USD;
} 
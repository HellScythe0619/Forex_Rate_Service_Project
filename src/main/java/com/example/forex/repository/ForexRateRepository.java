package com.example.forex.repository;

import com.example.forex.model.ForexRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ForexRateRepository extends MongoRepository<ForexRate, String> {
    List<ForexRate> findByDateBetweenOrderByDateAsc(LocalDateTime startDate, LocalDateTime endDate);
} 
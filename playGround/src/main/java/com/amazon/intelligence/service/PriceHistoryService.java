package com.amazon.intelligence.service;

import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.repository.PriceHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    public List<PriceSnapshot> getHistory(UUID productId) {
        return priceHistoryRepository.findByProductId(productId);
    }

    public Optional<PriceSnapshot> getLatest(UUID productId) {
        return priceHistoryRepository.findLatestByProductId(productId);
    }

    public PriceSnapshot save(PriceSnapshot snapshot) {
        return priceHistoryRepository.save(snapshot);
    }

    public void deleteByProductId(UUID productId) {
        priceHistoryRepository.deleteByProductId(productId);
    }
}

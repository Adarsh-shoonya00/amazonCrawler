package com.amazon.intelligence.repository;

import com.amazon.intelligence.domain.PriceSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceHistoryRepository {

    List<PriceSnapshot> findByProductId(UUID productId);

    Optional<PriceSnapshot> findLatestByProductId(UUID productId);

    PriceSnapshot save(PriceSnapshot snapshot);

    void deleteByProductId(UUID productId);
}

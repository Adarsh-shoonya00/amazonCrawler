package com.amazon.intelligence.repository;

import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    List<Product> findAll();

    List<Product> findByType(ProductType type);

    List<Product> findCompetitorsByOwnProductId(UUID ownProductId);

    Optional<Product> findById(UUID id);

    Optional<Product> findByAsin(String asin);

    Product save(Product product);

    void deleteById(UUID id);

    boolean existsByAsin(String asin);

    boolean existsByAsinExcludingId(String asin, UUID excludeId);
}

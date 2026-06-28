package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.repository.PriceHistoryRepository;
import com.amazon.intelligence.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class InMemoryStore implements ProductRepository, PriceHistoryRepository {

    private final ConcurrentHashMap<UUID, Product> products = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<PriceSnapshot>> priceHistory = new ConcurrentHashMap<>();

    @Override
    public List<Product> findAll() {
        return products.values().stream()
                .sorted(Comparator.comparing(Product::getAsin))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByType(ProductType type) {
        return products.values().stream()
                .filter(p -> p.getType() == type)
                .sorted(Comparator.comparing(Product::getAsin))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findCompetitorsByOwnProductId(UUID ownProductId) {
        return products.values().stream()
                .filter(p -> p.getType() == ProductType.COMPETITOR
                        && ownProductId.equals(p.getLinkedOwnProductId()))
                .sorted(Comparator.comparing(Product::getAsin))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Optional<Product> findByAsin(String asin) {
        return products.values().stream()
                .filter(p -> p.getAsin().equalsIgnoreCase(asin))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public void deleteById(UUID id) {
        products.remove(id);
        priceHistory.remove(id);
    }

    @Override
    public boolean existsByAsin(String asin) {
        return products.values().stream()
                .anyMatch(p -> p.getAsin().equalsIgnoreCase(asin));
    }

    @Override
    public boolean existsByAsinExcludingId(String asin, UUID excludeId) {
        return products.values().stream()
                .anyMatch(p -> !p.getId().equals(excludeId) && p.getAsin().equalsIgnoreCase(asin));
    }

    @Override
    public List<PriceSnapshot> findByProductId(UUID productId) {
        return priceHistory.getOrDefault(productId, List.of()).stream()
                .sorted(Comparator.comparing(PriceSnapshot::getCrawledAt))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PriceSnapshot> findLatestByProductId(UUID productId) {
        return findByProductId(productId).stream()
                .max(Comparator.comparing(PriceSnapshot::getCrawledAt));
    }

    @Override
    public PriceSnapshot save(PriceSnapshot snapshot) {
        priceHistory.computeIfAbsent(snapshot.getProductId(), id -> new CopyOnWriteArrayList<>())
                .add(snapshot);
        return snapshot;
    }

    @Override
    public void deleteByProductId(UUID productId) {
        priceHistory.remove(productId);
    }

    public ConcurrentHashMap<UUID, Product> getProducts() {
        return products;
    }

    public ConcurrentHashMap<UUID, List<PriceSnapshot>> getPriceHistory() {
        return priceHistory;
    }

    public void loadProducts(List<Product> productList) {
        products.clear();
        for (Product product : productList) {
            products.put(product.getId(), product);
        }
    }

    public void loadPriceHistory(ConcurrentHashMap<UUID, List<PriceSnapshot>> history) {
        priceHistory.clear();
        priceHistory.putAll(history);
    }
}

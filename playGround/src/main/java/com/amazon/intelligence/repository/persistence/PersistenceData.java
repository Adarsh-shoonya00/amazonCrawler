package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersistenceData {

    private List<Product> products = new ArrayList<>();
    private Map<UUID, List<PriceSnapshot>> priceHistory = new HashMap<>();

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Map<UUID, List<PriceSnapshot>> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(Map<UUID, List<PriceSnapshot>> priceHistory) {
        this.priceHistory = priceHistory;
    }
}

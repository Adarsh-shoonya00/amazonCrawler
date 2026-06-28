package com.amazon.intelligence.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PriceSnapshot {

    private UUID id;
    private UUID productId;
    private BigDecimal price;
    private String currency;
    private String seller;
    private Instant crawledAt;

    public PriceSnapshot() {
    }

    public PriceSnapshot(UUID id, UUID productId, BigDecimal price, String currency, String seller, Instant crawledAt) {
        this.id = id;
        this.productId = productId;
        this.price = price;
        this.currency = currency;
        this.seller = seller;
        this.crawledAt = crawledAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public Instant getCrawledAt() {
        return crawledAt;
    }

    public void setCrawledAt(Instant crawledAt) {
        this.crawledAt = crawledAt;
    }
}

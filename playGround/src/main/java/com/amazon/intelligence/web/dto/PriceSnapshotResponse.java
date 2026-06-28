package com.amazon.intelligence.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PriceSnapshotResponse {

    private UUID id;
    private BigDecimal price;
    private String currency;
    private String seller;
    private Instant crawledAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

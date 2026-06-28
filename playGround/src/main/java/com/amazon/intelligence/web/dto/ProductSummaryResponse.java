package com.amazon.intelligence.web.dto;

import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductSummaryResponse {

    private UUID id;
    private String asin;
    private String name;
    private ProductType type;
    private CrawlStatus lastCrawlStatus;
    private Instant lastCrawlAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public CrawlStatus getLastCrawlStatus() {
        return lastCrawlStatus;
    }

    public void setLastCrawlStatus(CrawlStatus lastCrawlStatus) {
        this.lastCrawlStatus = lastCrawlStatus;
    }

    public Instant getLastCrawlAt() {
        return lastCrawlAt;
    }

    public void setLastCrawlAt(Instant lastCrawlAt) {
        this.lastCrawlAt = lastCrawlAt;
    }
}

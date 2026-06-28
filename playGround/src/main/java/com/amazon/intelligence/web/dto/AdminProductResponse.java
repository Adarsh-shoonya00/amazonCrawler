package com.amazon.intelligence.web.dto;

import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.ProductType;

import java.time.Instant;
import java.util.UUID;

public class AdminProductResponse {

    private UUID id;
    private String asin;
    private String name;
    private ProductType type;
    private UUID linkedOwnProductId;
    private String linkedOwnProductName;
    private CrawlStatus lastCrawlStatus;
    private Instant lastCrawlAt;
    private String lastCrawlError;

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

    public UUID getLinkedOwnProductId() {
        return linkedOwnProductId;
    }

    public void setLinkedOwnProductId(UUID linkedOwnProductId) {
        this.linkedOwnProductId = linkedOwnProductId;
    }

    public String getLinkedOwnProductName() {
        return linkedOwnProductName;
    }

    public void setLinkedOwnProductName(String linkedOwnProductName) {
        this.linkedOwnProductName = linkedOwnProductName;
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

    public String getLastCrawlError() {
        return lastCrawlError;
    }

    public void setLastCrawlError(String lastCrawlError) {
        this.lastCrawlError = lastCrawlError;
    }
}

package com.amazon.intelligence.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product {

    private UUID id;
    private String asin;
    private String name;
    private String description;
    private List<String> imageUrls = new ArrayList<>();
    private ProductType type;
    private UUID linkedOwnProductId;
    private Instant lastCrawlAt;
    private CrawlStatus lastCrawlStatus;
    private String lastCrawlError;

    public Product() {
    }

    public Product(UUID id, String asin, ProductType type) {
        this.id = id;
        this.asin = asin;
        this.type = type;
        this.lastCrawlStatus = CrawlStatus.PENDING;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
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

    public Instant getLastCrawlAt() {
        return lastCrawlAt;
    }

    public void setLastCrawlAt(Instant lastCrawlAt) {
        this.lastCrawlAt = lastCrawlAt;
    }

    public CrawlStatus getLastCrawlStatus() {
        return lastCrawlStatus;
    }

    public void setLastCrawlStatus(CrawlStatus lastCrawlStatus) {
        this.lastCrawlStatus = lastCrawlStatus;
    }

    public String getLastCrawlError() {
        return lastCrawlError;
    }

    public void setLastCrawlError(String lastCrawlError) {
        this.lastCrawlError = lastCrawlError;
    }
}

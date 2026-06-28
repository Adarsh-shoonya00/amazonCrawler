package com.amazon.intelligence.web.dto;

import com.amazon.intelligence.domain.CrawlStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductDetailResponse {

    private UUID id;
    private String asin;
    private String name;
    private String description;
    private List<String> imageUrls;
    private BigDecimal currentPrice;
    private String currency;
    private String seller;
    private CrawlStatus lastCrawlStatus;
    private Instant lastCrawlAt;
    private List<CompetitorPriceResponse> competitors;

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
        this.imageUrls = imageUrls;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
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

    public List<CompetitorPriceResponse> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<CompetitorPriceResponse> competitors) {
        this.competitors = competitors;
    }
}

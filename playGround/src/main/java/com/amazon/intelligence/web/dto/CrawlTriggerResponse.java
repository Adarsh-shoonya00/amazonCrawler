package com.amazon.intelligence.web.dto;

import java.util.List;

public class CrawlTriggerResponse {

    private int crawledCount;
    private List<AdminProductResponse> products;

    public CrawlTriggerResponse(int crawledCount, List<AdminProductResponse> products) {
        this.crawledCount = crawledCount;
        this.products = products;
    }

    public int getCrawledCount() {
        return crawledCount;
    }

    public void setCrawledCount(int crawledCount) {
        this.crawledCount = crawledCount;
    }

    public List<AdminProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<AdminProductResponse> products) {
        this.products = products;
    }
}

package com.amazon.intelligence.master;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CatalogEntry {

    private String asin;
    private String name;
    private String description;
    private List<String> imageUrls = new ArrayList<>();
    private BigDecimal basePrice;
    private String currency;
    private String seller;

    public CatalogEntry() {
    }

    public CatalogEntry(String asin, String name, String description, List<String> imageUrls,
                        BigDecimal basePrice, String currency, String seller) {
        this.asin = asin;
        this.name = name;
        this.description = description;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.basePrice = basePrice;
        this.currency = currency;
        this.seller = seller;
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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
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
}

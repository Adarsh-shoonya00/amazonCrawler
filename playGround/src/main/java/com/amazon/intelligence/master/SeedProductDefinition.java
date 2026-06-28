package com.amazon.intelligence.master;

import com.amazon.intelligence.domain.ProductType;

public class SeedProductDefinition {

    private String asin;
    private ProductType type;
    private String linkedOwnAsin;

    public SeedProductDefinition() {
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getLinkedOwnAsin() {
        return linkedOwnAsin;
    }

    public void setLinkedOwnAsin(String linkedOwnAsin) {
        this.linkedOwnAsin = linkedOwnAsin;
    }
}

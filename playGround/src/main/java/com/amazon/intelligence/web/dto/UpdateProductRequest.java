package com.amazon.intelligence.web.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProductRequest {

    @NotBlank
    private String asin;

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }
}

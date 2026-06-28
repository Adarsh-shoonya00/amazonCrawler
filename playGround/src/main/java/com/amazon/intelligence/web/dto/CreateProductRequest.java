package com.amazon.intelligence.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateProductRequest {

    @NotBlank
    private String asin;

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }
}

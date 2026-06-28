package com.amazon.intelligence.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID id) {
        super("Product not found: " + id);
    }

    public ProductNotFoundException(String asin) {
        super("Product not found for ASIN: " + asin);
    }
}

package com.amazon.intelligence.exception;

public class DuplicateAsinException extends RuntimeException {

    public DuplicateAsinException(String asin) {
        super("Product with ASIN already exists: " + asin);
    }
}

package com.amazon.intelligence.exception;

import java.util.UUID;

public class InvalidProductLinkException extends RuntimeException {

    public InvalidProductLinkException(UUID linkedOwnProductId) {
        super("Linked own product not found or invalid: " + linkedOwnProductId);
    }
}

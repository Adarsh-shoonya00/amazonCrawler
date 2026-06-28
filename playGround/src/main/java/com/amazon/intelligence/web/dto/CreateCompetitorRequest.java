package com.amazon.intelligence.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateCompetitorRequest {

    @NotBlank
    private String asin;

    @NotNull
    private UUID linkedOwnProductId;

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public UUID getLinkedOwnProductId() {
        return linkedOwnProductId;
    }

    public void setLinkedOwnProductId(UUID linkedOwnProductId) {
        this.linkedOwnProductId = linkedOwnProductId;
    }
}

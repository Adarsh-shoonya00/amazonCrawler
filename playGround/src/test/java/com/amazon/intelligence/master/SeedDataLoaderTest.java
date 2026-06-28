package com.amazon.intelligence.master;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.repository.persistence.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeedDataLoaderTest {

    @Test
    void seedIntoLoadsOwnAndCompetitorProducts() {
        AppProperties properties = new AppProperties();
        properties.getMaster().setSeedFile("classpath:seed-products.json");

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SeedDataLoader seedDataLoader = new SeedDataLoader(properties, objectMapper);
        InMemoryStore store = new InMemoryStore();

        seedDataLoader.seedInto(store);

        assertEquals(5, store.findAll().size());
        assertEquals(2, store.findByType(ProductType.OWN).size());
        assertEquals(3, store.findByType(ProductType.COMPETITOR).size());
    }
}

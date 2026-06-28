package com.amazon.intelligence.master;

import com.amazon.intelligence.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogRepositoryTest {

    private JsonCatalogRepository catalogRepository;

    @BeforeEach
    void setUp() throws Exception {
        AppProperties properties = new AppProperties();
        properties.getMaster().setCatalogFile("classpath:mock-catalog.json");

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        catalogRepository = new JsonCatalogRepository(properties, objectMapper);
        catalogRepository.loadCatalog();
    }

    @Test
    void findByAsinReturnsCatalogEntry() {
        CatalogEntry entry = catalogRepository.findByAsin("B08N5WRWNW").orElseThrow();

        assertEquals("B08N5WRWNW", entry.getAsin());
        assertEquals("Echo Dot (4th Gen) Smart Speaker", entry.getName());
        assertEquals(new BigDecimal("49.99"), entry.getBasePrice());
        assertEquals("USD", entry.getCurrency());
    }

    @Test
    void findByAsinReturnsEmptyForUnknownAsin() {
        assertTrue(catalogRepository.findByAsin("UNKNOWN").isEmpty());
    }
}

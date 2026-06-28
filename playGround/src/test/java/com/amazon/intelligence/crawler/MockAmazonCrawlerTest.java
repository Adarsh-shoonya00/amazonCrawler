package com.amazon.intelligence.crawler;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.exception.ProductNotFoundException;
import com.amazon.intelligence.master.JsonCatalogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MockAmazonCrawlerTest {

    private MockAmazonCrawler crawler;

    @BeforeEach
    void setUp() throws Exception {
        AppProperties properties = new AppProperties();
        properties.getCrawl().setPriceJitterPercent(0);
        properties.getCrawl().setSimulatedLatencyMsMin(0);
        properties.getCrawl().setSimulatedLatencyMsMax(0);
        properties.getMaster().setCatalogFile("classpath:mock-catalog.json");

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonCatalogRepository catalogRepository = new JsonCatalogRepository(properties, objectMapper);
        catalogRepository.loadCatalog();

        crawler = new MockAmazonCrawler(properties, catalogRepository);
    }

    @Test
    void crawlReturnsCatalogDataForKnownAsin() throws Exception {
        CrawlResult result = crawler.crawl("B08N5WRWNW");

        assertEquals("B08N5WRWNW", result.getAsin());
        assertEquals("Echo Dot (4th Gen) Smart Speaker", result.getName());
        assertEquals(new BigDecimal("49.99"), result.getPrice());
        assertNotNull(result.getImageUrls());
    }

    @Test
    void crawlThrowsForUnknownAsin() {
        assertThrows(ProductNotFoundException.class, () -> crawler.crawl("UNKNOWN123"));
    }
}

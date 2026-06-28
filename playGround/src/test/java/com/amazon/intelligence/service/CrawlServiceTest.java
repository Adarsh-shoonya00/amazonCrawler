package com.amazon.intelligence.service;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.crawler.CrawlResult;
import com.amazon.intelligence.crawler.ProductCrawler;
import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.exception.ProductNotFoundException;
import com.amazon.intelligence.repository.persistence.InMemoryStore;
import com.amazon.intelligence.repository.persistence.JsonFilePersistence;
import com.amazon.intelligence.repository.persistence.StoreCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrawlServiceTest {

    private InMemoryStore store;
    private CrawlService crawlService;
    private Product product;
    private AtomicInteger flushCount;

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        flushCount = new AtomicInteger();
        PriceHistoryService priceHistoryService = new PriceHistoryService(store);
        ProductCrawler crawler = asin -> {
            if (!asin.equals("B08N5WRWNW")) {
                throw new ProductNotFoundException(asin);
            }
            CrawlResult result = new CrawlResult();
            result.setAsin(asin);
            result.setName("Test Product");
            result.setDescription("Description");
            result.setImageUrls(List.of("http://example.com/img.jpg"));
            result.setPrice(new BigDecimal("10.00"));
            result.setCurrency("USD");
            result.setSeller("Amazon");
            return result;
        };

        JsonFilePersistence persistence = new JsonFilePersistence(store, new AppProperties()) {
            @Override
            public void flush() {
                flushCount.incrementAndGet();
            }
        };

        StoreCoordinator coordinator = new StoreCoordinator(persistence);
        crawlService = new CrawlService(store, priceHistoryService, crawler, coordinator);

        product = new Product(UUID.randomUUID(), "B08N5WRWNW", ProductType.OWN);
        store.save(product);
    }

    @Test
    void crawlProductUpdatesMetadataAndHistory() {
        Product result = crawlService.crawlProductAndFlush(product.getId());

        assertEquals(CrawlStatus.SUCCESS, result.getLastCrawlStatus());
        assertEquals("Test Product", result.getName());
        assertEquals(1, store.findByProductId(product.getId()).size());
        assertEquals(1, flushCount.get());
    }

    @Test
    void crawlProductMarksFailureForMissingCatalogEntry() {
        Product failing = new Product(UUID.randomUUID(), "MISSING", ProductType.OWN);
        store.save(failing);

        Product result = crawlService.crawlProductAndFlush(failing.getId());

        assertEquals(CrawlStatus.FAILED, result.getLastCrawlStatus());
        assertTrue(result.getLastCrawlError().contains("MISSING"));
        assertTrue(store.findByProductId(failing.getId()).isEmpty());
    }

    @Test
    void crawlAllFlushesOnce() {
        Product second = new Product(UUID.randomUUID(), "B08N5WRWNW", ProductType.COMPETITOR);
        second.setLinkedOwnProductId(product.getId());
        store.save(second);

        crawlService.crawlAll();

        assertEquals(1, flushCount.get());
    }
}

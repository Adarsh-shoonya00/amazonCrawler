package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonFilePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void flushAndLoadRoundTrip() {
        Path storePath = tempDir.resolve("store.json");

        AppProperties properties = new AppProperties();
        properties.getPersistence().setFile(storePath.toString());

        InMemoryStore store = new InMemoryStore();
        JsonFilePersistence persistence = new JsonFilePersistence(store, properties);

        Product product = new Product(UUID.randomUUID(), "B08N5WRWNW", ProductType.OWN);
        product.setName("Echo Dot");
        product.setLastCrawlStatus(CrawlStatus.SUCCESS);
        store.save(product);

        PriceSnapshot snapshot = new PriceSnapshot(
                UUID.randomUUID(),
                product.getId(),
                new BigDecimal("49.99"),
                "USD",
                "Amazon",
                Instant.parse("2025-01-01T00:00:00Z")
        );
        store.save(snapshot);

        persistence.flush();

        InMemoryStore reloaded = new InMemoryStore();
        JsonFilePersistence loader = new JsonFilePersistence(reloaded, properties);
        loader.loadFromFile();

        assertEquals(1, reloaded.findAll().size());
        assertEquals("Echo Dot", reloaded.findById(product.getId()).orElseThrow().getName());
        assertEquals(1, reloaded.findByProductId(product.getId()).size());
        assertEquals(new BigDecimal("49.99"), reloaded.findByProductId(product.getId()).get(0).getPrice());
    }
}

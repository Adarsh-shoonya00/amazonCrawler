package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreCoordinatorTest {

    @Test
    void mutateFlushesAfterAction() {
        InMemoryStore store = new InMemoryStore();
        AtomicInteger flushCount = new AtomicInteger();

        JsonFilePersistence persistence = new JsonFilePersistence(store, new AppProperties()) {
            @Override
            public void flush() {
                flushCount.incrementAndGet();
            }
        };

        StoreCoordinator coordinator = new StoreCoordinator(persistence);

        Product saved = coordinator.mutate(() -> {
            Product product = new Product(UUID.randomUUID(), "B08N5WRWNW", ProductType.OWN);
            store.save(product);
            return product;
        });

        assertEquals("B08N5WRWNW", saved.getAsin());
        assertEquals(1, flushCount.get());
    }
}

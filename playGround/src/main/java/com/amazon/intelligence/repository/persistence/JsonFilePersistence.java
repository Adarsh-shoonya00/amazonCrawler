package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class JsonFilePersistence {

    private static final Logger log = LoggerFactory.getLogger(JsonFilePersistence.class);

    private final InMemoryStore store;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public JsonFilePersistence(InMemoryStore store, AppProperties appProperties) {
        this.store = store;
        this.appProperties = appProperties;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public boolean storeFileExists() {
        return Files.exists(Path.of(appProperties.getPersistence().getFile()));
    }

    public void loadFromFile() {
        Path storePath = Path.of(appProperties.getPersistence().getFile());
        if (!Files.exists(storePath)) {
            return;
        }
        try {
            PersistenceData data = objectMapper.readValue(storePath.toFile(), PersistenceData.class);
            store.loadProducts(data.getProducts());

            ConcurrentHashMap<UUID, java.util.List<PriceSnapshot>> history = new ConcurrentHashMap<>();
            if (data.getPriceHistory() != null) {
                data.getPriceHistory().forEach((id, snapshots) ->
                        history.put(id, new CopyOnWriteArrayList<>(snapshots)));
            }
            store.loadPriceHistory(history);
            log.info("Loaded {} products from {}", store.getProducts().size(), storePath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load store from file: " + storePath, e);
        }
    }

    public void flush() {
        try {
            Path storePath = Path.of(appProperties.getPersistence().getFile());
            Files.createDirectories(storePath.getParent());

            PersistenceData data = new PersistenceData();
            data.setProducts(store.findAll());
            data.setPriceHistory(new ConcurrentHashMap<>(store.getPriceHistory()));

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storePath.toFile(), data);
        } catch (IOException e) {
            log.error("Failed to persist store to file", e);
        }
    }
}

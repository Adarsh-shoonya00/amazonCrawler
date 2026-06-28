package com.amazon.intelligence.master;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.repository.persistence.InMemoryStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SeedDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SeedDataLoader.class);

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public SeedDataLoader(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    public void seedInto(InMemoryStore store) {
        List<SeedProductDefinition> definitions = loadDefinitions();
        Map<String, UUID> ownAsinToId = new HashMap<>();

        for (SeedProductDefinition definition : definitions) {
            if (definition.getType() == ProductType.OWN) {
                Product product = createProduct(definition.getAsin(), ProductType.OWN, null);
                store.save(product);
                ownAsinToId.put(definition.getAsin(), product.getId());
            }
        }

        for (SeedProductDefinition definition : definitions) {
            if (definition.getType() == ProductType.COMPETITOR) {
                UUID linkedOwnProductId = ownAsinToId.get(definition.getLinkedOwnAsin());
                Product product = createProduct(
                        definition.getAsin(),
                        ProductType.COMPETITOR,
                        linkedOwnProductId
                );
                store.save(product);
            }
        }

        log.info("Seeded {} products from {}", definitions.size(), appProperties.getMaster().getSeedFile());
    }

    private List<SeedProductDefinition> loadDefinitions() {
        String location = appProperties.getMaster().getSeedFile();
        try (InputStream input = ResourceLoader.open(location)) {
            return objectMapper.readValue(input, new TypeReference<List<SeedProductDefinition>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load seed file: " + location, e);
        }
    }

    private Product createProduct(String asin, ProductType type, UUID linkedOwnProductId) {
        Product product = new Product(UUID.randomUUID(), asin, type);
        product.setLinkedOwnProductId(linkedOwnProductId);
        product.setLastCrawlStatus(CrawlStatus.PENDING);
        return product;
    }
}

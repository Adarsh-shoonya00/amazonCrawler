package com.amazon.intelligence.master;

import com.amazon.intelligence.config.AppProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JsonCatalogRepository implements CatalogRepository {

    private static final Logger log = LoggerFactory.getLogger(JsonCatalogRepository.class);

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private Map<String, CatalogEntry> catalog = Map.of();

    public JsonCatalogRepository(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadCatalog() throws IOException {
        String location = appProperties.getMaster().getCatalogFile();
        try (InputStream input = ResourceLoader.open(location)) {
            Map<String, Map<String, Object>> raw = objectMapper.readValue(
                    input, new TypeReference<Map<String, Map<String, Object>>>() {});

            Map<String, CatalogEntry> loaded = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : raw.entrySet()) {
                Map<String, Object> value = entry.getValue();
                CatalogEntry catalogEntry = new CatalogEntry(
                        entry.getKey(),
                        (String) value.get("name"),
                        (String) value.get("description"),
                        (List<String>) value.get("images"),
                        new BigDecimal(value.get("basePrice").toString()),
                        (String) value.getOrDefault("currency", "USD"),
                        (String) value.get("seller")
                );
                loaded.put(entry.getKey(), catalogEntry);
            }
            catalog = Collections.unmodifiableMap(loaded);
            log.info("Loaded {} catalog entries from {}", catalog.size(), location);
        }
    }

    @Override
    public Optional<CatalogEntry> findByAsin(String asin) {
        return Optional.ofNullable(catalog.get(asin));
    }
}

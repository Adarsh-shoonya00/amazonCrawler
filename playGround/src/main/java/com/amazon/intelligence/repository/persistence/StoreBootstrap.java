package com.amazon.intelligence.repository.persistence;

import com.amazon.intelligence.master.SeedDataLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StoreBootstrap {

    private static final Logger log = LoggerFactory.getLogger(StoreBootstrap.class);

    private final InMemoryStore store;
    private final JsonFilePersistence jsonFilePersistence;
    private final SeedDataLoader seedDataLoader;
    private final StoreCoordinator storeCoordinator;

    public StoreBootstrap(InMemoryStore store,
                          JsonFilePersistence jsonFilePersistence,
                          SeedDataLoader seedDataLoader,
                          StoreCoordinator storeCoordinator) {
        this.store = store;
        this.jsonFilePersistence = jsonFilePersistence;
        this.seedDataLoader = seedDataLoader;
        this.storeCoordinator = storeCoordinator;
    }

    @PostConstruct
    public void initialize() {
        if (jsonFilePersistence.storeFileExists()) {
            jsonFilePersistence.loadFromFile();
        } else {
            storeCoordinator.mutate(() -> seedDataLoader.seedInto(store));
            log.info("Seeded initial transactional data from master seed file");
        }
    }
}

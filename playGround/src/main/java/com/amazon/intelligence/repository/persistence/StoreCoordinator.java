package com.amazon.intelligence.repository.persistence;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class StoreCoordinator {

    private final JsonFilePersistence jsonFilePersistence;
    private final Object lock = new Object();

    public StoreCoordinator(JsonFilePersistence jsonFilePersistence) {
        this.jsonFilePersistence = jsonFilePersistence;
    }

    public <T> T mutate(Supplier<T> action) {
        synchronized (lock) {
            T result = action.get();
            jsonFilePersistence.flush();
            return result;
        }
    }

    public void mutate(Runnable action) {
        mutate(() -> {
            action.run();
            return null;
        });
    }
}

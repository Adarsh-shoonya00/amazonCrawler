package com.amazon.intelligence.master;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class ResourceLoader {

    private ResourceLoader() {
    }

    static InputStream open(String location) throws IOException {
        if (location.startsWith("classpath:")) {
            String path = location.substring("classpath:".length());
            return new ClassPathResource(path).getInputStream();
        }
        if (location.startsWith("file:")) {
            return Files.newInputStream(Path.of(location.substring("file:".length())));
        }
        return Files.newInputStream(Path.of(location));
    }
}

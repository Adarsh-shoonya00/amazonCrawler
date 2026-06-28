package com.amazon.intelligence.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CrawlConfigValidator {

    private static final String SUPPORTED_IMPL = "mock";

    private final AppProperties appProperties;

    public CrawlConfigValidator(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void validate() {
        String impl = appProperties.getCrawl().getImpl();
        if (!SUPPORTED_IMPL.equals(impl)) {
            throw new IllegalStateException(
                    "Unsupported app.crawl.impl: " + impl + ". Supported values: " + SUPPORTED_IMPL);
        }
    }
}

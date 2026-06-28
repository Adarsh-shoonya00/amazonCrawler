package com.amazon.intelligence.config;

import com.amazon.intelligence.crawler.MockAmazonCrawler;
import com.amazon.intelligence.crawler.ProductCrawler;
import com.amazon.intelligence.master.CatalogRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.crawl", name = "impl", havingValue = "mock", matchIfMissing = true)
public class MockCrawlerConfiguration {

    @Bean
    public ProductCrawler productCrawler(AppProperties appProperties, CatalogRepository catalogRepository) {
        return new MockAmazonCrawler(appProperties, catalogRepository);
    }
}

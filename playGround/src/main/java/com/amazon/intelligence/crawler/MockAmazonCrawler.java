package com.amazon.intelligence.crawler;

import com.amazon.intelligence.config.AppProperties;
import com.amazon.intelligence.exception.ProductNotFoundException;
import com.amazon.intelligence.master.CatalogEntry;
import com.amazon.intelligence.master.CatalogRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class MockAmazonCrawler implements ProductCrawler {

    private final AppProperties appProperties;
    private final CatalogRepository catalogRepository;

    public MockAmazonCrawler(AppProperties appProperties, CatalogRepository catalogRepository) {
        this.appProperties = appProperties;
        this.catalogRepository = catalogRepository;
    }

    @Override
    public CrawlResult crawl(String asin) throws InterruptedException {
        simulateLatency();

        CatalogEntry entry = catalogRepository.findByAsin(asin)
                .orElseThrow(() -> new ProductNotFoundException(asin));

        CrawlResult result = new CrawlResult();
        result.setAsin(asin);
        result.setName(entry.getName());
        result.setDescription(entry.getDescription());
        result.setImageUrls(entry.getImageUrls());
        result.setCurrency(entry.getCurrency() != null ? entry.getCurrency() : "USD");
        result.setSeller(entry.getSeller());
        result.setPrice(applyJitter(entry.getBasePrice()));

        return result;
    }

    private BigDecimal applyJitter(BigDecimal basePrice) {
        int jitterPercent = appProperties.getCrawl().getPriceJitterPercent();
        if (jitterPercent <= 0) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }
        double factor = 1.0 + (ThreadLocalRandom.current().nextDouble(-jitterPercent, jitterPercent) / 100.0);
        return basePrice.multiply(BigDecimal.valueOf(factor)).setScale(2, RoundingMode.HALF_UP);
    }

    private void simulateLatency() throws InterruptedException {
        int min = appProperties.getCrawl().getSimulatedLatencyMsMin();
        int max = appProperties.getCrawl().getSimulatedLatencyMsMax();
        int delay = ThreadLocalRandom.current().nextInt(min, max + 1);
        Thread.sleep(delay);
    }
}

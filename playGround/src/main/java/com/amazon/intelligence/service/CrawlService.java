package com.amazon.intelligence.service;

import com.amazon.intelligence.crawler.CrawlResult;
import com.amazon.intelligence.crawler.ProductCrawler;
import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.exception.ProductNotFoundException;
import com.amazon.intelligence.repository.ProductRepository;
import com.amazon.intelligence.repository.persistence.StoreCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CrawlService {

    private static final Logger log = LoggerFactory.getLogger(CrawlService.class);

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;
    private final ProductCrawler productCrawler;
    private final StoreCoordinator storeCoordinator;

    public CrawlService(ProductRepository productRepository,
                        PriceHistoryService priceHistoryService,
                        ProductCrawler productCrawler,
                        StoreCoordinator storeCoordinator) {
        this.productRepository = productRepository;
        this.priceHistoryService = priceHistoryService;
        this.productCrawler = productCrawler;
        this.storeCoordinator = storeCoordinator;
    }

    public List<Product> crawlAll() {
        return storeCoordinator.mutate(() -> {
            List<Product> results = new ArrayList<>();
            for (Product product : productRepository.findAll()) {
                results.add(crawlProductInternal(product.getId()));
            }
            return results;
        });
    }

    public Product crawlProduct(UUID productId) {
        return crawlProductInternal(productId);
    }

    public Product crawlProductAndFlush(UUID productId) {
        return storeCoordinator.mutate(() -> crawlProductInternal(productId));
    }

    private Product crawlProductInternal(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        try {
            CrawlResult result = productCrawler.crawl(product.getAsin());
            applyCrawlResult(product, result);
            product.setLastCrawlAt(Instant.now());
            product.setLastCrawlStatus(CrawlStatus.SUCCESS);
            product.setLastCrawlError(null);

            PriceSnapshot snapshot = new PriceSnapshot(
                    UUID.randomUUID(),
                    product.getId(),
                    result.getPrice(),
                    result.getCurrency(),
                    result.getSeller(),
                    Instant.now()
            );
            priceHistoryService.save(snapshot);
            log.info("Crawl succeeded for ASIN {} (product {})", product.getAsin(), product.getId());
        } catch (Exception e) {
            product.setLastCrawlAt(Instant.now());
            product.setLastCrawlStatus(CrawlStatus.FAILED);
            product.setLastCrawlError(e.getMessage());
            log.error("Crawl failed for ASIN {} (product {}): {}", product.getAsin(), product.getId(), e.getMessage());
        }

        productRepository.save(product);
        return product;
    }

    private void applyCrawlResult(Product product, CrawlResult result) {
        product.setName(result.getName());
        product.setDescription(result.getDescription());
        product.setImageUrls(result.getImageUrls());
    }
}

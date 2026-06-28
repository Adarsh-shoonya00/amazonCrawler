package com.amazon.intelligence.service;

import com.amazon.intelligence.domain.CrawlStatus;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.exception.DuplicateAsinException;
import com.amazon.intelligence.exception.InvalidProductLinkException;
import com.amazon.intelligence.exception.ProductNotFoundException;
import com.amazon.intelligence.repository.ProductRepository;
import com.amazon.intelligence.repository.persistence.StoreCoordinator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;
    private final StoreCoordinator storeCoordinator;

    public ProductService(ProductRepository productRepository,
                          PriceHistoryService priceHistoryService,
                          StoreCoordinator storeCoordinator) {
        this.productRepository = productRepository;
        this.priceHistoryService = priceHistoryService;
        this.storeCoordinator = storeCoordinator;
    }

    public List<Product> listOwnProducts() {
        return productRepository.findByType(ProductType.OWN);
    }

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getCompetitors(UUID ownProductId) {
        getProduct(ownProductId);
        return productRepository.findCompetitorsByOwnProductId(ownProductId);
    }

    public Product createOwnProduct(String asin) {
        return storeCoordinator.mutate(() -> {
            validateUniqueAsin(asin);
            Product product = new Product(UUID.randomUUID(), asin.trim().toUpperCase(), ProductType.OWN);
            product.setLastCrawlStatus(CrawlStatus.PENDING);
            productRepository.save(product);
            return product;
        });
    }

    public Product updateOwnProduct(UUID id, String asin) {
        return storeCoordinator.mutate(() -> {
            Product product = getProduct(id);
            if (product.getType() != ProductType.OWN) {
                throw new IllegalArgumentException("Product is not an own product: " + id);
            }
            if (productRepository.existsByAsinExcludingId(asin, id)) {
                throw new DuplicateAsinException(asin);
            }
            product.setAsin(asin.trim().toUpperCase());
            productRepository.save(product);
            return product;
        });
    }

    public void deleteOwnProduct(UUID id) {
        storeCoordinator.mutate(() -> {
            Product product = getProduct(id);
            if (product.getType() != ProductType.OWN) {
                throw new IllegalArgumentException("Product is not an own product: " + id);
            }
            List<Product> competitors = productRepository.findCompetitorsByOwnProductId(id);
            for (Product competitor : competitors) {
                deleteCompetitorInternal(competitor.getId());
            }
            priceHistoryService.deleteByProductId(id);
            productRepository.deleteById(id);
        });
    }

    public Product createCompetitor(String asin, UUID linkedOwnProductId) {
        return storeCoordinator.mutate(() -> {
            validateUniqueAsin(asin);
            validateOwnProductLink(linkedOwnProductId);

            Product product = new Product(UUID.randomUUID(), asin.trim().toUpperCase(), ProductType.COMPETITOR);
            product.setLinkedOwnProductId(linkedOwnProductId);
            product.setLastCrawlStatus(CrawlStatus.PENDING);
            productRepository.save(product);
            return product;
        });
    }

    public Product updateCompetitor(UUID id, String asin, UUID linkedOwnProductId) {
        return storeCoordinator.mutate(() -> {
            Product product = getProduct(id);
            if (product.getType() != ProductType.COMPETITOR) {
                throw new IllegalArgumentException("Product is not a competitor: " + id);
            }
            if (productRepository.existsByAsinExcludingId(asin, id)) {
                throw new DuplicateAsinException(asin);
            }
            validateOwnProductLink(linkedOwnProductId);
            product.setAsin(asin.trim().toUpperCase());
            product.setLinkedOwnProductId(linkedOwnProductId);
            productRepository.save(product);
            return product;
        });
    }

    public void deleteCompetitor(UUID id) {
        storeCoordinator.mutate(() -> deleteCompetitorInternal(id));
    }

    private void deleteCompetitorInternal(UUID id) {
        Product product = getProduct(id);
        if (product.getType() != ProductType.COMPETITOR) {
            throw new IllegalArgumentException("Product is not a competitor: " + id);
        }
        priceHistoryService.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    private void validateUniqueAsin(String asin) {
        if (productRepository.existsByAsin(asin)) {
            throw new DuplicateAsinException(asin);
        }
    }

    private void validateOwnProductLink(UUID linkedOwnProductId) {
        Product ownProduct = productRepository.findById(linkedOwnProductId)
                .orElseThrow(() -> new InvalidProductLinkException(linkedOwnProductId));
        if (ownProduct.getType() != ProductType.OWN) {
            throw new InvalidProductLinkException(linkedOwnProductId);
        }
    }
}

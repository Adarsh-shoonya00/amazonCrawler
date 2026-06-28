package com.amazon.intelligence.web.mapper;

import com.amazon.intelligence.domain.PriceSnapshot;
import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.repository.ProductRepository;
import com.amazon.intelligence.service.PriceHistoryService;
import com.amazon.intelligence.web.dto.AdminProductResponse;
import com.amazon.intelligence.web.dto.CompetitorPriceResponse;
import com.amazon.intelligence.web.dto.PriceSnapshotResponse;
import com.amazon.intelligence.web.dto.ProductDetailResponse;
import com.amazon.intelligence.web.dto.ProductSummaryResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final PriceHistoryService priceHistoryService;
    private final ProductRepository productRepository;

    public ProductMapper(PriceHistoryService priceHistoryService, ProductRepository productRepository) {
        this.priceHistoryService = priceHistoryService;
        this.productRepository = productRepository;
    }

    public ProductSummaryResponse toSummary(Product product) {
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.setId(product.getId());
        response.setAsin(product.getAsin());
        response.setName(product.getName());
        response.setType(product.getType());
        response.setLastCrawlStatus(product.getLastCrawlStatus());
        response.setLastCrawlAt(product.getLastCrawlAt());
        return response;
    }

    public ProductDetailResponse toDetail(Product product, List<Product> competitors) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setAsin(product.getAsin());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setImageUrls(product.getImageUrls());
        response.setLastCrawlStatus(product.getLastCrawlStatus());
        response.setLastCrawlAt(product.getLastCrawlAt());

        Optional<PriceSnapshot> latest = priceHistoryService.getLatest(product.getId());
        latest.ifPresent(snapshot -> {
            response.setCurrentPrice(snapshot.getPrice());
            response.setCurrency(snapshot.getCurrency());
            response.setSeller(snapshot.getSeller());
        });

        BigDecimal ownPrice = response.getCurrentPrice();
        response.setCompetitors(competitors.stream()
                .map(c -> toCompetitorPrice(c, ownPrice))
                .collect(Collectors.toList()));

        return response;
    }

    public AdminProductResponse toAdmin(Product product) {
        AdminProductResponse response = new AdminProductResponse();
        response.setId(product.getId());
        response.setAsin(product.getAsin());
        response.setName(product.getName());
        response.setType(product.getType());
        response.setLinkedOwnProductId(product.getLinkedOwnProductId());
        response.setLastCrawlStatus(product.getLastCrawlStatus());
        response.setLastCrawlAt(product.getLastCrawlAt());
        response.setLastCrawlError(product.getLastCrawlError());

        if (product.getLinkedOwnProductId() != null) {
            productRepository.findById(product.getLinkedOwnProductId())
                    .ifPresent(own -> response.setLinkedOwnProductName(own.getName()));
        }

        return response;
    }

    public PriceSnapshotResponse toSnapshotResponse(PriceSnapshot snapshot) {
        PriceSnapshotResponse response = new PriceSnapshotResponse();
        response.setId(snapshot.getId());
        response.setPrice(snapshot.getPrice());
        response.setCurrency(snapshot.getCurrency());
        response.setSeller(snapshot.getSeller());
        response.setCrawledAt(snapshot.getCrawledAt());
        return response;
    }

    private CompetitorPriceResponse toCompetitorPrice(Product competitor, BigDecimal ownPrice) {
        CompetitorPriceResponse response = new CompetitorPriceResponse();
        response.setId(competitor.getId());
        response.setAsin(competitor.getAsin());
        response.setName(competitor.getName());

        Optional<PriceSnapshot> latest = priceHistoryService.getLatest(competitor.getId());
        latest.ifPresent(snapshot -> {
            response.setCurrentPrice(snapshot.getPrice());
            response.setCurrency(snapshot.getCurrency());
            response.setSeller(snapshot.getSeller());
            if (ownPrice != null && snapshot.getPrice() != null) {
                response.setPriceDelta(snapshot.getPrice().subtract(ownPrice));
            }
        });

        return response;
    }
}

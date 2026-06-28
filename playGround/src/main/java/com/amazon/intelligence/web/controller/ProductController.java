package com.amazon.intelligence.web.controller;

import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.domain.ProductType;
import com.amazon.intelligence.service.PriceHistoryService;
import com.amazon.intelligence.service.ProductService;
import com.amazon.intelligence.web.dto.PriceSnapshotResponse;
import com.amazon.intelligence.web.dto.ProductDetailResponse;
import com.amazon.intelligence.web.dto.ProductSummaryResponse;
import com.amazon.intelligence.web.mapper.ProductMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PriceHistoryService priceHistoryService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService,
                             PriceHistoryService priceHistoryService,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.priceHistoryService = priceHistoryService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public List<ProductSummaryResponse> listOwnProducts() {
        return productService.listOwnProducts().stream()
                .map(productMapper::toSummary)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        if (product.getType() != ProductType.OWN) {
            throw new IllegalArgumentException("Only own products have a public detail page: " + id);
        }
        List<Product> competitors = productService.getCompetitors(id);
        return productMapper.toDetail(product, competitors);
    }

    @GetMapping("/{id}/price-history")
    public List<PriceSnapshotResponse> getPriceHistory(@PathVariable UUID id) {
        productService.getProduct(id);
        return priceHistoryService.getHistory(id).stream()
                .map(productMapper::toSnapshotResponse)
                .collect(Collectors.toList());
    }
}

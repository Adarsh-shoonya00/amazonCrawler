package com.amazon.intelligence.web.controller;

import com.amazon.intelligence.domain.Product;
import com.amazon.intelligence.service.CrawlService;
import com.amazon.intelligence.service.ProductService;
import com.amazon.intelligence.web.dto.AdminProductResponse;
import com.amazon.intelligence.web.dto.CreateCompetitorRequest;
import com.amazon.intelligence.web.dto.CreateProductRequest;
import com.amazon.intelligence.web.dto.CrawlTriggerResponse;
import com.amazon.intelligence.web.dto.UpdateCompetitorRequest;
import com.amazon.intelligence.web.dto.UpdateProductRequest;
import com.amazon.intelligence.web.mapper.ProductMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final CrawlService crawlService;
    private final ProductMapper productMapper;

    public AdminController(ProductService productService,
                           CrawlService crawlService,
                           ProductMapper productMapper) {
        this.productService = productService;
        this.crawlService = crawlService;
        this.productMapper = productMapper;
    }

    @GetMapping("/products")
    public List<AdminProductResponse> listAllProducts() {
        return productService.listAllProducts().stream()
                .map(productMapper::toAdmin)
                .collect(Collectors.toList());
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createOwnProduct(request.getAsin());
        return productMapper.toAdmin(product);
    }

    @PutMapping("/products/{id}")
    public AdminProductResponse updateProduct(@PathVariable UUID id,
                                              @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateOwnProduct(id, request.getAsin());
        return productMapper.toAdmin(product);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteOwnProduct(id);
    }

    @PostMapping("/competitors")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminProductResponse createCompetitor(@Valid @RequestBody CreateCompetitorRequest request) {
        Product product = productService.createCompetitor(request.getAsin(), request.getLinkedOwnProductId());
        return productMapper.toAdmin(product);
    }

    @PutMapping("/competitors/{id}")
    public AdminProductResponse updateCompetitor(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateCompetitorRequest request) {
        Product product = productService.updateCompetitor(id, request.getAsin(), request.getLinkedOwnProductId());
        return productMapper.toAdmin(product);
    }

    @DeleteMapping("/competitors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompetitor(@PathVariable UUID id) {
        productService.deleteCompetitor(id);
    }

    @PostMapping("/crawl")
    public CrawlTriggerResponse crawlAll() {
        List<Product> products = crawlService.crawlAll();
        List<AdminProductResponse> responses = products.stream()
                .map(productMapper::toAdmin)
                .collect(Collectors.toList());
        return new CrawlTriggerResponse(products.size(), responses);
    }

    @PostMapping("/crawl/{productId}")
    public AdminProductResponse crawlProduct(@PathVariable UUID productId) {
        Product product = crawlService.crawlProductAndFlush(productId);
        return productMapper.toAdmin(product);
    }
}

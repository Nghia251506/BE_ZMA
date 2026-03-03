package com.example.demo.Controller;

import com.example.demo.Dto.Product.ProductRequest;
import com.example.demo.Dto.Product.ProductResponse;
import com.example.demo.Service.Product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.search(keyword, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
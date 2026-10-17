package com.mealio.product.infrastructure.web;

import com.mealio.product.application.ProductService;
import com.mealio.product.infrastructure.web.dto.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        var product = service.create(java.util.Optional.ofNullable(request.gtin()), request.toDraft());
        return ResponseEntity.created(URI.create("/api/v1/products/" + product.id())).body(ProductResponse.from(product));
    }

    @GetMapping
    public List<ProductResponse> search(@RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.search(query, page, size).stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable UUID id) { return ProductResponse.from(service.get(id)); }

    @GetMapping("/by-gtin/{gtin}")
    public ProductResponse byGtin(@PathVariable String gtin) { return ProductResponse.from(service.byGtin(gtin)); }

    @GetMapping("/{id}/versions/{version}")
    public ProductResponse version(@PathVariable UUID id, @PathVariable int version) {
        return ProductResponse.from(service.getVersion(id, version));
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<ProductResponse> revise(@PathVariable UUID id, @RequestBody ProductVersionRequest request) {
        var draft = request.toDraft();
        var product = service.revise(id, request.expectedVersion(), draft);
        return ResponseEntity.created(URI.create("/api/v1/products/" + id + "/versions/" + product.version().number()))
                .body(ProductResponse.from(product));
    }
}

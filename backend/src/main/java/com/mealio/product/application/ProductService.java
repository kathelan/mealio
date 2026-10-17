package com.mealio.product.application;

import com.mealio.product.api.ProductCatalog;
import com.mealio.product.api.ProductNotFoundException;
import com.mealio.product.api.ProductSnapshot;
import com.mealio.product.application.port.out.ProductRepository;
import com.mealio.product.domain.*;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements ProductCatalog {
    private final ProductRepository repository;
    private final Clock clock;

    @Transactional
    public Product create(Optional<String> rawGtin, ProductDraft draft) {
        var gtin = rawGtin.map(Gtin::of);
        var now = Instant.now(clock).truncatedTo(ChronoUnit.MICROS);
        var source = gtin.map(code -> ProductSourceSnapshot.Source.USER_LABEL).orElse(ProductSourceSnapshot.Source.USER_INPUT);
        return repository.create(Product.builder().id(UUID.randomUUID()).gtin(gtin).createdAt(now)
                .version(ProductVersion.builder().id(UUID.randomUUID()).number(1).details(draft)
                        .source(ProductSourceSnapshot.builder().source(source).recordedAt(now).build()).build()).build());
    }

    public Product get(UUID id) {
        return repository.find(id, OptionalInt.empty()).orElseThrow(ProductNotFoundException::new);
    }

    public Product getVersion(UUID id, int versionNumber) {
        if (versionNumber < 1) throw new InvalidProductException("Version must be positive");
        return repository.find(id, OptionalInt.of(versionNumber)).orElseThrow(ProductNotFoundException::new);
    }

    public Product byGtin(String rawGtin) {
        return repository.findByGtin(Gtin.of(rawGtin)).orElseThrow(ProductNotFoundException::new);
    }

    public List<Product> search(String query, int page, int size) {
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE || Optional.ofNullable(query).map(String::length).orElse(221) > 220) {
            throw new InvalidProductException("Query must have at most 220 characters, page >= 0 and size between 1 and 100");
        }
        return repository.search(query.trim(), page, size);
    }

    @Transactional
    public Product revise(UUID id, int expectedVersion, ProductDraft draft) {
        return repository.appendVersion(get(id).revise(expectedVersion, UUID.randomUUID(), draft,
                Instant.now(clock).truncatedTo(ChronoUnit.MICROS)));
    }

    @Override
    public ProductSnapshot snapshot(UUID productId) { return toSnapshot(get(productId)); }

    @Override
    public ProductSnapshot snapshotVersion(UUID productId, int versionNumber) {
        return toSnapshot(getVersion(productId, versionNumber));
    }

    private ProductSnapshot toSnapshot(Product product) {
        var version = product.version();
        var details = version.details();
        var nutrition = details.nutrition();
        return ProductSnapshot.builder().id(product.id()).gtin(product.gtin().map(Gtin::value))
                .createdAt(product.createdAt()).versionId(version.id()).versionNumber(version.number())
                .name(details.name()).brand(details.brand()).basis(nutrition.basis().name())
                .energyKcal(nutrition.energyKcal()).protein(nutrition.protein()).fat(nutrition.fat())
                .carbohydrates(nutrition.carbohydrates()).verificationStatus(nutrition.status().name())
                .source(version.source().source().name()).recordedAt(version.source().recordedAt()).build();
    }
}

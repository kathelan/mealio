package com.mealio.product.infrastructure.persistence;

import com.mealio.product.api.ProductUnavailableException;
import com.mealio.product.application.port.out.ProductRepository;
import com.mealio.product.domain.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {
    private final ProductJpaRepository products;
    private final ProductVersionJpaRepository versions;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product create(Product product) {
        try {
            var entity = ProductEntity.builder().id(product.id()).gtin(product.gtin().map(Gtin::value).orElse(null))
                    .createdAt(product.createdAt()).currentVersion(1).build();
            products.saveAndFlush(entity);
            var version = versions.saveAndFlush(mapper.versionEntity(product));
            return mapper.toDomain(entity, version);
        } catch (DataIntegrityViolationException exception) {
            if (constraint(exception, "uk_products_gtin")) {
                throw new ProductConflictException("GTIN already exists");
            }
            throw new ProductUnavailableException(exception);
        } catch (DataAccessException exception) { throw new ProductUnavailableException(exception); }
    }

    @Override
    public Product appendVersion(Product product) {
        try {
            var entity = products.findById(product.id()).orElseThrow(() -> new ProductConflictException("Product has changed"));
            if (entity.getCurrentVersion() != product.version().number() - 1) {
                throw new ProductConflictException("Product version has changed");
            }
            entity.setCurrentVersion(product.version().number());
            products.saveAndFlush(entity);
            var version = versions.saveAndFlush(mapper.versionEntity(product));
            return mapper.toDomain(entity, version);
        } catch (OptimisticLockingFailureException exception) {
            throw new ProductConflictException("Product version has changed");
        } catch (DataIntegrityViolationException exception) {
            if (constraint(exception, "uk_product_versions_number")) {
                throw new ProductConflictException("Product version has changed");
            }
            throw new ProductUnavailableException(exception);
        } catch (DataAccessException exception) { throw new ProductUnavailableException(exception); }
    }

    @Override
    public Optional<Product> find(UUID id, java.util.OptionalInt versionNumber) {
        try {
            return products.findById(id).flatMap(product -> version(product,
                    versionNumber.orElseGet(product::getCurrentVersion)));
        } catch (DataAccessException exception) { throw new ProductUnavailableException(exception); }
    }

    @Override
    public Optional<Product> findByGtin(Gtin gtin) {
        try {
            return products.findByGtin(gtin.value()).flatMap(product -> version(product, product.getCurrentVersion()));
        } catch (DataAccessException exception) { throw new ProductUnavailableException(exception); }
    }

    @Override
    public List<Product> search(String query, int page, int size) {
        var escaped = query.toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
        try {
            return products.search("%" + escaped + "%", PageRequest.of(page, size)).stream()
                    .map(row -> mapper.toDomain(row.product(), row.version())).toList();
        } catch (DataAccessException exception) { throw new ProductUnavailableException(exception); }
    }

    private boolean constraint(DataIntegrityViolationException exception, String name) {
        return String.valueOf(exception.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT).contains(name);
    }

    private Optional<Product> version(ProductEntity product, int number) {
        return versions.findByProductIdAndVersionNumber(product.getId(), number).map(version -> mapper.toDomain(product, version));
    }
}

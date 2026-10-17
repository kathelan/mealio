package com.mealio.product.application.port.out;

import com.mealio.product.domain.Gtin;
import com.mealio.product.domain.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product create(Product product);
    Product appendVersion(Product product);
    Optional<Product> find(UUID id, java.util.OptionalInt versionNumber);
    Optional<Product> findByGtin(Gtin gtin);
    List<Product> search(String query, int page, int size);
}

package com.mealio.product.api;

import java.util.UUID;

public interface ProductCatalog {
    ProductSnapshot snapshot(UUID productId);
    ProductSnapshot snapshotVersion(UUID productId, int versionNumber);
}

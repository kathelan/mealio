package com.mealio.product.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVersionJpaRepository extends JpaRepository<ProductVersionEntity, UUID> {
    Optional<ProductVersionEntity> findByProductIdAndVersionNumber(UUID productId, int versionNumber);
}

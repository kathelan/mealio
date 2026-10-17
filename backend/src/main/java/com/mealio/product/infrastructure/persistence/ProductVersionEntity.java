package com.mealio.product.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_versions")
@Getter
@lombok.Builder
@lombok.NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductVersionEntity {
    @Id private UUID id;
    @Column(nullable = false, updatable = false) private UUID productId;
    @Column(nullable = false, updatable = false) private int versionNumber;
    @Column(nullable = false, length = 220, updatable = false) private String name;
    @Column(length = 160, updatable = false) private String brand;
    @Column(nullable = false, length = 16, updatable = false) private String basis;
    @Column(precision = 12, scale = 4, updatable = false) private BigDecimal energyKcal;
    @Column(precision = 12, scale = 4, updatable = false) private BigDecimal protein;
    @Column(precision = 12, scale = 4, updatable = false) private BigDecimal fat;
    @Column(precision = 12, scale = 4, updatable = false) private BigDecimal carbohydrates;
    @Column(nullable = false, length = 20, updatable = false) private String verificationStatus;
    @Column(nullable = false, length = 16, updatable = false) private String source;
    @Column(nullable = false, updatable = false) private Instant recordedAt;
}

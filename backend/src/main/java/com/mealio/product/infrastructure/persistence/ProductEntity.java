package com.mealio.product.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@lombok.Builder
@lombok.NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductEntity {
    @Id private UUID id;
    @Column(length = 14, unique = true) private String gtin;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Setter @Column(nullable = false) private int currentVersion;
    @Version private Long revision;
}

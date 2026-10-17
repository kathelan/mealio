package com.mealio.product.infrastructure.persistence;

import com.mealio.product.domain.*;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {
    public Product toDomain(ProductEntity product, ProductVersionEntity version) {
        var nutrition = NutritionFacts.builder().basis(NutritionBasis.valueOf(version.getBasis()))
                .energyKcal(Optional.ofNullable(version.getEnergyKcal())).protein(Optional.ofNullable(version.getProtein()))
                .fat(Optional.ofNullable(version.getFat())).carbohydrates(Optional.ofNullable(version.getCarbohydrates())).build();
        return Product.builder().id(product.getId()).gtin(Optional.ofNullable(product.getGtin()).map(Gtin::of))
                .createdAt(product.getCreatedAt()).version(ProductVersion.builder().id(version.getId())
                        .number(version.getVersionNumber()).details(ProductDraft.builder().name(version.getName())
                                .brand(Optional.ofNullable(version.getBrand())).nutrition(nutrition).build())
                        .source(ProductSourceSnapshot.builder().source(ProductSourceSnapshot.Source.valueOf(version.getSource()))
                                .recordedAt(version.getRecordedAt()).build()).build()).build();
    }

    public ProductVersionEntity versionEntity(Product product) {
        var version = product.version();
        var details = version.details();
        var nutrition = details.nutrition();
        return ProductVersionEntity.builder().id(version.id()).productId(product.id()).versionNumber(version.number())
                .name(details.name()).brand(details.brand().orElse(null)).basis(nutrition.basis().name())
                .energyKcal(nutrition.energyKcal().orElse(null)).protein(nutrition.protein().orElse(null))
                .fat(nutrition.fat().orElse(null)).carbohydrates(nutrition.carbohydrates().orElse(null))
                .verificationStatus(nutrition.status().name()).source(version.source().source().name())
                .recordedAt(version.source().recordedAt()).build();
    }
}

package com.mealio.recipe.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recipes")
@NoArgsConstructor
public class RecipeEntity extends BaseEntity {

    @Column(nullable = false, length = 220)
    private String name;

    @Column(length = 2048)
    private String description;

    private LocalDate createdOn;

    @Column(length = 1024)
    private String imageUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recipe_categories",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "category_id", nullable = false)
    private Set<java.util.UUID> categoryIds = new LinkedHashSet<>();
}

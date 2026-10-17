package com.mealio.adapter.out.persistence.entity;

import com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_categories",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<MealCategoryEntity> categories = new LinkedHashSet<>();
}

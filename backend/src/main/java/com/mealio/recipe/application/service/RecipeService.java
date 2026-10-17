package com.mealio.recipe.application.service;

import com.mealio.category.api.CategoryLookup;
import com.mealio.category.api.CategoryLookupUnavailableException;
import com.mealio.recipe.domain.exception.RecipePersistenceException;
import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeIngredient;
import com.mealio.recipe.domain.exception.InvalidRecipeException;
import com.mealio.recipe.domain.exception.RecipeNotFoundException;
import com.mealio.recipe.domain.model.AddIngredientCommand;
import com.mealio.recipe.domain.model.CreateRecipeCommand;
import com.mealio.recipe.domain.model.RecipeDetails;
import com.mealio.recipe.domain.port.in.ManageRecipesUseCase;
import com.mealio.recipe.domain.port.out.RecipeRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RecipeService implements ManageRecipesUseCase {
    private final RecipeRepository repository;
    private final Clock clock;
    private final CategoryLookup categoryLookup;

    @Override
    @Transactional(readOnly = true)
    public List<Recipe> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Recipe::createdOn, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Recipe::name))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDetails get(UUID id) {
        var recipe = repository.findById(id).orElseThrow(() -> new RecipeNotFoundException(id));
        return new RecipeDetails(recipe, repository.findIngredients(id));
    }

    @Override
    @Transactional
    public Recipe create(CreateRecipeCommand command) {
        if (command == null) throw new InvalidRecipeException("Recipe payload is required");
        var name = normalize(command.name(), "Recipe name", 220);
        var description = optional(command.description(), "Recipe description", 2048);
        var imageUrl = optional(command.imageUrl(), "Recipe image URL", 1024);
        var categoryIds = command.categoryIds() == null ? new LinkedHashSet<UUID>() : new LinkedHashSet<>(command.categoryIds());
        if (categoryIds.contains(null)) throw new InvalidRecipeException("Category ids cannot contain null");
        Set<UUID> existingIds;
        try {
            existingIds = categoryLookup.findExistingIds(categoryIds);
        } catch (CategoryLookupUnavailableException exception) {
            throw new RecipePersistenceException("Cannot validate recipe categories", exception);
        }
        if (!existingIds.equals(categoryIds)) {
            var missing = new LinkedHashSet<>(categoryIds);
            missing.removeAll(existingIds);
            throw new InvalidRecipeException("Categories not found: " + missing);
        }
        var createdOn = command.createdOn() == null ? LocalDate.now(clock) : command.createdOn();
        return repository.save(new Recipe(null, name, description, createdOn, imageUrl, categoryIds));
    }

    @Override
    @Transactional
    public RecipeIngredient addIngredient(AddIngredientCommand command) {
        if (command == null || command.recipeId() == null) {
            throw new InvalidRecipeException("Recipe id is required");
        }
        repository.findById(command.recipeId()).orElseThrow(() -> new RecipeNotFoundException(command.recipeId()));
        var name = normalize(command.name(), "Ingredient name", 220);
        if (command.quantity() == null || command.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRecipeException("Ingredient quantity must be greater than zero");
        }
        ensureDecimalFits(command.quantity(), "Ingredient quantity", 12, 3);
        if (command.unit() == null) throw new InvalidRecipeException("Ingredient unit is required");
        if (command.calories() != null && command.calories().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecipeException("Ingredient calories cannot be negative");
        }
        if (command.calories() != null) ensureDecimalFits(command.calories(), "Ingredient calories", 10, 2);
        return repository.saveIngredient(new RecipeIngredient(null, command.recipeId(), name,
                command.quantity(), command.unit(), command.calories()));
    }

    private String normalize(String value, String label, int maxLength) {
        if (!StringUtils.hasText(value)) throw new InvalidRecipeException(label + " is required");
        var normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > maxLength) throw new InvalidRecipeException(label + " must be at most " + maxLength + " characters");
        return normalized;
    }

    private String optional(String value, String label, int maxLength) {
        if (value == null) return null;
        return normalize(value, label, maxLength);
    }

    private void ensureDecimalFits(BigDecimal value, String label, int precision, int scale) {
        var normalized = value.stripTrailingZeros();
        var actualScale = Math.max(normalized.scale(), 0);
        var integerDigits = Math.max(normalized.precision() - normalized.scale(), 0);
        if (actualScale > scale || integerDigits > precision - scale) {
            throw new InvalidRecipeException(label + " must fit numeric(" + precision + "," + scale + ")");
        }
    }
}

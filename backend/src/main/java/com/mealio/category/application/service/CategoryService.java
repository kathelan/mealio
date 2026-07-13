package com.mealio.category.application.service;

import com.mealio.category.domain.exception.CategoryNotFoundException;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.exception.CategoryHasChildrenException;
import com.mealio.category.domain.exception.InvalidCategoryException;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.CreateCategoryCommand;
import com.mealio.category.domain.model.FieldPatch;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.domain.model.PatchCategoryCommand;
import com.mealio.category.domain.port.in.ManageCategoriesUseCase;
import com.mealio.category.domain.port.out.CategoryRepository;
import java.text.Normalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CategoryService implements ManageCategoriesUseCase {

    private static final int MAX_NAME_LENGTH = 160;
    private static final int MAX_PATH_LENGTH = 512;
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9]+");
    private static final Pattern SLUG_EDGES = Pattern.compile("(^-+|-+$)");

    private final CategoryRepository categoryRepository;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public List<MealCategory> list() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(MealCategory::path))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MealCategory get(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    @Transactional
    public MealCategory create(CreateCategoryCommand command) {
        if (command == null) {
            throw new InvalidCategoryException("Category payload is required");
        }
        var name = normalizeName(command.name());
        var type = command.type() == null ? CategoryType.CUSTOM : command.type();
        var sortOrder = command.sortOrder() == null ? 0 : command.sortOrder();
        var parent = command.parentId() == null ? null : get(command.parentId());
        var slug = slugify(name);
        var path = path(parent, slug);
        ensureUniquePath(path, null);
        var now = Instant.now(clock);

        return categoryRepository.save(new MealCategory(
                null,
                parent == null ? null : parent.id(),
                name,
                slug,
                path,
                type,
                sortOrder,
                now,
                now
        ));
    }

    @Override
    @Transactional
    public MealCategory patch(PatchCategoryCommand command) {
        if (command == null || command.id() == null) {
            throw new InvalidCategoryException("Category id is required");
        }
        if (!hasAnyField(command)) {
            throw new InvalidCategoryException("Category patch payload must contain at least one supported field");
        }
        var existing = get(command.id());
        var nextName = valueOrExisting(command.name(), existing.name());
        if (command.name().present()) {
            nextName = normalizeName(nextName);
        }
        var nextType = valueOrExisting(command.type(), existing.type());
        if (nextType == null) {
            throw new InvalidCategoryException("Category type is required");
        }
        var nextSortOrder = valueOrExisting(command.sortOrder(), existing.sortOrder());
        var nextParentId = valueOrExisting(command.parentId(), existing.parentId());
        if (existing.id().equals(nextParentId)) {
            throw new InvalidCategoryException("Category cannot be its own parent");
        }

        var parent = nextParentId == null ? null : get(nextParentId);
        var nextSlug = command.name().present() ? slugify(nextName) : existing.slug();
        var nextPath = path(parent, nextSlug);
        if (isDescendantPath(parent, existing.path())) {
            throw new InvalidCategoryException("Category cannot be moved under its descendant");
        }
        if (isNoop(existing, nextParentId, nextName, nextSlug, nextPath, nextType, nextSortOrder)) {
            return existing;
        }
        ensureUniquePath(nextPath, existing.id());
        var now = Instant.now(clock);
        var updated = new MealCategory(
                existing.id(),
                nextParentId,
                nextName,
                nextSlug,
                nextPath,
                nextType,
                nextSortOrder,
                existing.createdAt(),
                now
        );

        var descendants = categoryRepository.findByPathPrefix(existing.path() + "/").stream()
                .map(descendant -> moveDescendant(descendant, existing.path(), nextPath, now))
                .toList();
        categoryRepository.save(updated);
        if (!descendants.isEmpty()) {
            categoryRepository.saveAll(descendants);
        }
        return get(existing.id());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        get(id);
        if (categoryRepository.existsByParentId(id)) {
            throw new CategoryHasChildrenException(id);
        }
        categoryRepository.deleteById(id);
    }

    private boolean hasAnyField(PatchCategoryCommand command) {
        return command.name().present()
                || command.parentId().present()
                || command.type().present()
                || command.sortOrder().present();
    }

    private boolean isNoop(
            MealCategory existing,
            UUID nextParentId,
            String nextName,
            String nextSlug,
            String nextPath,
            CategoryType nextType,
            Integer nextSortOrder
    ) {
        return Objects.equals(existing.parentId(), nextParentId)
                && Objects.equals(existing.name(), nextName)
                && Objects.equals(existing.slug(), nextSlug)
                && Objects.equals(existing.path(), nextPath)
                && Objects.equals(existing.type(), nextType)
                && Objects.equals(existing.sortOrder(), nextSortOrder);
    }

    private String normalizeName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidCategoryException("Category name is required");
        }
        var normalized = name.trim().replaceAll("\\s+", " ");
        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new InvalidCategoryException("Category name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        return normalized;
    }

    private String slugify(String name) {
        var withoutDiacritics = DIACRITICS.matcher(Normalizer.normalize(name, Normalizer.Form.NFD))
                .replaceAll("");
        var slug = SLUG_EDGES.matcher(NON_SLUG_CHARS.matcher(withoutDiacritics.toLowerCase(Locale.ROOT))
                .replaceAll("-")).replaceAll("");
        if (!StringUtils.hasText(slug)) {
            throw new InvalidCategoryException("Category name must contain at least one slug character");
        }
        return slug;
    }

    private String path(MealCategory parent, String slug) {
        var path = parent == null ? slug : parent.path() + "/" + slug;
        if (path.length() > MAX_PATH_LENGTH) {
            throw new InvalidCategoryException("Category path must be at most " + MAX_PATH_LENGTH + " characters");
        }
        return path;
    }

    private void ensureUniquePath(String path, UUID currentId) {
        categoryRepository.findByPath(path)
                .filter(category -> !category.id().equals(currentId))
                .ifPresent(category -> {
                    throw new DuplicateCategoryException(path);
                });
    }

    private boolean isDescendantPath(MealCategory parent, String currentPath) {
        return parent != null && parent.path().startsWith(currentPath + "/");
    }

    private MealCategory moveDescendant(MealCategory descendant, String oldRootPath, String newRootPath, Instant now) {
        var movedPath = newRootPath + descendant.path().substring(oldRootPath.length());
        return new MealCategory(
                descendant.id(),
                descendant.parentId(),
                descendant.name(),
                descendant.slug(),
                movedPath,
                descendant.type(),
                descendant.sortOrder(),
                descendant.createdAt(),
                now
        );
    }

    private <T> T valueOrExisting(FieldPatch<T> patch, T existing) {
        return patch.present() ? patch.value() : existing;
    }
}

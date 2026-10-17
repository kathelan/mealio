package com.mealio.category.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mealio.category.domain.exception.CategoryHasChildrenException;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.exception.InvalidCategoryException;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.CreateCategoryCommand;
import com.mealio.category.domain.model.FieldPatch;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.domain.model.PatchCategoryCommand;
import com.mealio.category.domain.port.out.CategoryRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryServiceTest {

    private final InMemoryCategoryRepository repository = new InMemoryCategoryRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-07T10:00:00Z"), ZoneOffset.UTC);
    private final CategoryService service = new CategoryService(repository, clock);

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void createsCategoryWithGeneratedSlugAndDefaults() {
        var category = service.create(new CreateCategoryCommand(null, "  Śniadanie  ", null, null));

        assertThat(category.id()).isNotNull();
        assertThat(category.name()).isEqualTo("Śniadanie");
        assertThat(category.slug()).isEqualTo("sniadanie");
        assertThat(category.path()).isEqualTo("sniadanie");
        assertThat(category.type()).isEqualTo(CategoryType.CUSTOM);
        assertThat(category.sortOrder()).isZero();
    }

    @Test
    void rejectsDuplicatePath() {
        service.create(new CreateCategoryCommand(null, "Breakfast", CategoryType.MEAL, 0));

        assertThatThrownBy(() -> service.create(new CreateCategoryCommand(null, "Breakfast", CategoryType.MEAL, 1)))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessageContaining("breakfast");
    }

    @Test
    void patchesCategoryAndMovesDescendantPaths() {
        var meal = service.create(new CreateCategoryCommand(null, "Meal", CategoryType.MEAL, 1));
        var soup = service.create(new CreateCategoryCommand(meal.id(), "Soup", CategoryType.CUSTOM, 2));
        var tomato = service.create(new CreateCategoryCommand(soup.id(), "Tomato", CategoryType.CUSTOM, 3));

        var updated = service.patch(new PatchCategoryCommand(
                soup.id(),
                FieldPatch.of("Sweet Soup"),
                FieldPatch.missing(),
                FieldPatch.of(CategoryType.TASTE),
                FieldPatch.of(7)
        ));

        assertThat(updated.path()).isEqualTo("meal/sweet-soup");
        assertThat(updated.type()).isEqualTo(CategoryType.TASTE);
        assertThat(updated.sortOrder()).isEqualTo(7);
        assertThat(service.get(tomato.id()).path()).isEqualTo("meal/sweet-soup/tomato");
    }

    @Test
    void patchCanMoveCategoryToRoot() {
        var meal = service.create(new CreateCategoryCommand(null, "Meal", CategoryType.MEAL, 1));
        var soup = service.create(new CreateCategoryCommand(meal.id(), "Soup", CategoryType.CUSTOM, 2));

        var updated = service.patch(new PatchCategoryCommand(
                soup.id(),
                FieldPatch.missing(),
                FieldPatch.of(null),
                FieldPatch.missing(),
                FieldPatch.missing()
        ));

        assertThat(updated.parentId()).isNull();
        assertThat(updated.path()).isEqualTo("soup");
    }

    @Test
    void rejectsMovingCategoryUnderDescendant() {
        var meal = service.create(new CreateCategoryCommand(null, "Meal", CategoryType.MEAL, 1));
        var soup = service.create(new CreateCategoryCommand(meal.id(), "Soup", CategoryType.CUSTOM, 2));

        assertThatThrownBy(() -> service.patch(new PatchCategoryCommand(
                meal.id(),
                FieldPatch.missing(),
                FieldPatch.of(soup.id()),
                FieldPatch.missing(),
                FieldPatch.missing()
        ))).isInstanceOf(InvalidCategoryException.class)
                .hasMessageContaining("descendant");
    }

    @Test
    void rejectsEmptyPatch() {
        var meal = service.create(new CreateCategoryCommand(null, "Meal", CategoryType.MEAL, 1));

        assertThatThrownBy(() -> service.patch(new PatchCategoryCommand(
                meal.id(),
                FieldPatch.missing(),
                FieldPatch.missing(),
                FieldPatch.missing(),
                FieldPatch.missing()
        ))).isInstanceOf(InvalidCategoryException.class)
                .hasMessageContaining("at least one");
    }

    @Test
    void rejectsDeletingCategoryWithChildren() {
        var meal = service.create(new CreateCategoryCommand(null, "Meal", CategoryType.MEAL, 1));
        service.create(new CreateCategoryCommand(meal.id(), "Soup", CategoryType.CUSTOM, 2));

        assertThatThrownBy(() -> service.delete(meal.id()))
                .isInstanceOf(CategoryHasChildrenException.class);
    }

    @Test
    void listsByPathAndAllowsTheSameSlugUnderDifferentParents() {
        var lunch = service.create(new CreateCategoryCommand(null, "Lunch", CategoryType.MEAL, 2));
        var breakfast = service.create(new CreateCategoryCommand(null, "Breakfast", CategoryType.MEAL, 1));
        service.create(new CreateCategoryCommand(lunch.id(), "Sweet", CategoryType.TASTE, 0));
        service.create(new CreateCategoryCommand(breakfast.id(), "Sweet", CategoryType.TASTE, 0));

        assertThat(service.list()).extracting(MealCategory::path)
                .containsExactly("breakfast", "breakfast/sweet", "lunch", "lunch/sweet");
    }

    @Test
    void noOpPatchPreservesTimestampsAndDescendants() {
        var parent = service.create(new CreateCategoryCommand(null, "Lunch", CategoryType.MEAL, 0));
        var child = service.create(new CreateCategoryCommand(parent.id(), "Soup", CategoryType.CUSTOM, 0));
        var laterService = new CategoryService(repository, Clock.offset(clock, java.time.Duration.ofHours(1)));

        var unchanged = laterService.patch(new PatchCategoryCommand(parent.id(), FieldPatch.of("Lunch"),
                FieldPatch.missing(), FieldPatch.missing(), FieldPatch.missing()));

        assertThat(unchanged).isEqualTo(parent);
        assertThat(service.get(child.id())).isEqualTo(child);
    }

    @Test
    void validatesRequiredPayloadIdNameTypeAndTreeDepth() {
        assertThatThrownBy(() -> service.create(null)).isInstanceOf(InvalidCategoryException.class);
        for (var name : java.util.Arrays.asList(null, " ", "x".repeat(161), "!!!")) {
            assertThatThrownBy(() -> service.create(new CreateCategoryCommand(null, name, null, null)))
                    .isInstanceOf(InvalidCategoryException.class);
        }
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(com.mealio.category.domain.exception.CategoryNotFoundException.class);
        assertThatThrownBy(() -> service.patch(null)).isInstanceOf(InvalidCategoryException.class);
        assertThatThrownBy(() -> service.patch(new PatchCategoryCommand(null, FieldPatch.of("x"),
                FieldPatch.missing(), FieldPatch.missing(), FieldPatch.missing())))
                .isInstanceOf(InvalidCategoryException.class);
        var root = service.create(new CreateCategoryCommand(null, "Root", null, null));
        assertThatThrownBy(() -> service.patch(new PatchCategoryCommand(root.id(), FieldPatch.missing(),
                FieldPatch.missing(), FieldPatch.of(null), FieldPatch.missing())))
                .isInstanceOf(InvalidCategoryException.class).hasMessageContaining("type");
        assertThatThrownBy(() -> service.patch(new PatchCategoryCommand(root.id(), FieldPatch.missing(),
                FieldPatch.of(root.id()), FieldPatch.missing(), FieldPatch.missing())))
                .isInstanceOf(InvalidCategoryException.class).hasMessageContaining("own parent");
        var second = service.create(new CreateCategoryCommand(root.id(), "a".repeat(160), null, null));
        var third = service.create(new CreateCategoryCommand(second.id(), "b".repeat(160), null, null));
        var fourth = service.create(new CreateCategoryCommand(third.id(), "c".repeat(160), null, null));
        assertThatThrownBy(() -> service.create(new CreateCategoryCommand(fourth.id(), "d".repeat(30), null, null)))
                .isInstanceOf(InvalidCategoryException.class).hasMessageContaining("path");
        service.delete(fourth.id());
        assertThatThrownBy(() -> service.get(fourth.id()))
                .isInstanceOf(com.mealio.category.domain.exception.CategoryNotFoundException.class);
    }

    private static final class InMemoryCategoryRepository implements CategoryRepository {

        private final List<MealCategory> categories = new ArrayList<>();

        @Override
        public List<MealCategory> findAll() {
            return List.copyOf(categories);
        }

        @Override
        public Set<UUID> findExistingIds(Set<UUID> ids) {
            return categories.stream().map(MealCategory::id).filter(ids::contains)
                    .collect(java.util.stream.Collectors.toSet());
        }

        @Override
        public Optional<MealCategory> findById(UUID id) {
            return categories.stream().filter(category -> category.id().equals(id)).findFirst();
        }

        @Override
        public Optional<MealCategory> findByPath(String path) {
            return categories.stream().filter(category -> category.path().equals(path)).findFirst();
        }

        @Override
        public List<MealCategory> findByPathPrefix(String pathPrefix) {
            return categories.stream().filter(category -> category.path().startsWith(pathPrefix)).toList();
        }

        @Override
        public boolean existsByParentId(UUID parentId) {
            return categories.stream().anyMatch(category -> parentId.equals(category.parentId()));
        }

        @Override
        public MealCategory save(MealCategory category) {
            var saved = category.id() == null
                    ? new MealCategory(
                    UUID.randomUUID(),
                    category.parentId(),
                    category.name(),
                    category.slug(),
                    category.path(),
                    category.type(),
                    category.sortOrder(),
                    category.createdAt(),
                    category.updatedAt()
            )
                    : category;
            deleteById(saved.id());
            categories.add(saved);
            return saved;
        }

        @Override
        public List<MealCategory> saveAll(List<MealCategory> categoriesToSave) {
            return categoriesToSave.stream().map(this::save).toList();
        }

        @Override
        public void deleteById(UUID id) {
            categories.removeIf(category -> category.id().equals(id));
        }

        void clear() {
            categories.clear();
        }
    }
}

package com.mealio.category.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity;
import com.mealio.category.infrastructure.persistence.mapper.MealCategoryPersistenceMapper;
import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

class JpaCategoryRepositoryTest {
    private final MealCategoryJpaRepository jpa = mock(MealCategoryJpaRepository.class);
    private final EntityManager em = mock(EntityManager.class);
    private final MealCategoryPersistenceMapper mapper = new MealCategoryPersistenceMapper();
    private final JpaCategoryRepository repository = new JpaCategoryRepository(jpa, mapper, em);
    private final UUID id = UUID.randomUUID();
    private final MealCategory category = category(id, null);

    private MealCategory category(UUID categoryId, UUID parent) {
        return new MealCategory(categoryId, parent, "Lunch", "lunch", "lunch", CategoryType.MEAL,
                1, Instant.EPOCH, Instant.EPOCH);
    }

    private MealCategoryEntity entity() {
        var result = new MealCategoryEntity();
        mapper.copyToEntity(category, result, null);
        return result;
    }

    @Test
    void readsMapsSavesUpdatesAndDeletesCategories() {
        var entity = entity();
        when(jpa.findAll()).thenReturn(List.of(entity));
        when(jpa.findAllById(Set.of(id))).thenReturn(List.of(entity));
        when(jpa.findById(id)).thenReturn(Optional.of(entity));
        when(jpa.findByPath("lunch")).thenReturn(Optional.of(entity));
        when(jpa.findByPathStartingWith("lunch/")).thenReturn(List.of(entity));
        when(jpa.existsByParent_Id(id)).thenReturn(true);
        when(jpa.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        assertThat(repository.findAll()).containsExactly(category);
        assertThat(repository.findExistingIds(Set.of(id))).containsExactly(id);
        assertThat(repository.findById(id)).contains(category);
        assertThat(repository.findByPath("lunch")).contains(category);
        assertThat(repository.findByPathPrefix("lunch/")).containsExactly(category);
        assertThat(repository.existsByParentId(id)).isTrue();
        assertThat(repository.save(category)).isEqualTo(category);
        assertThat(repository.save(category(null, null)).id()).isNull();
        var parent = entity();
        when(em.getReference(MealCategoryEntity.class, id)).thenReturn(parent);
        var child = category(UUID.randomUUID(), id);
        assertThat(repository.save(child)).isEqualTo(child);
        assertThat(repository.saveAll(List.of(category))).containsExactly(category);
        repository.deleteById(id);
        verify(jpa).deleteById(id);
    }

    @Test
    void translatesEveryDatabaseReadAndDeleteFailure() {
        var failure = new DataAccessResourceFailureException("offline");
        when(jpa.findAll()).thenThrow(failure);
        when(jpa.findAllById(any())).thenThrow(failure);
        when(jpa.findById(any())).thenThrow(failure);
        when(jpa.findByPath(any())).thenThrow(failure);
        when(jpa.findByPathStartingWith(any())).thenThrow(failure);
        when(jpa.existsByParent_Id(any())).thenThrow(failure);
        doThrow(failure).when(jpa).deleteById(any());
        for (Runnable operation : List.<Runnable>of(() -> repository.findAll(),
                () -> repository.findExistingIds(Set.of(id)), () -> repository.findById(id),
                () -> repository.findByPath("lunch"), () -> repository.findByPathPrefix("lunch/"),
                () -> repository.existsByParentId(id), () -> repository.deleteById(id))) {
            assertThatThrownBy(operation::run).isInstanceOf(CategoryPersistenceException.class).hasCause(failure);
        }
    }

    @Test
    void preservesUniqueConstraintMeaningAndWrapsOtherWriteFailures() {
        for (var constraint : new String[]{"uk_meal_categories_path", "UK_MEAL_CATEGORIES_PARENT_SLUG"}) {
            doThrow(new DataIntegrityViolationException(constraint)).when(jpa).saveAndFlush(any());
            assertThatThrownBy(() -> repository.save(category)).isInstanceOf(DuplicateCategoryException.class);
        }
        var failure = new DataAccessResourceFailureException("offline");
        doThrow(failure).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> repository.save(category)).isInstanceOf(CategoryPersistenceException.class).hasCause(failure);
        assertThatThrownBy(() -> repository.saveAll(List.of(category)))
                .isInstanceOf(CategoryPersistenceException.class).hasCause(failure);
    }
}

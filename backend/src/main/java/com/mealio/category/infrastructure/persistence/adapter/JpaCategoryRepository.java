package com.mealio.category.infrastructure.persistence.adapter;

import com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity;
import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.domain.port.out.CategoryRepository;
import com.mealio.category.infrastructure.persistence.mapper.MealCategoryPersistenceMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final MealCategoryJpaRepository jpaRepository;
    private final MealCategoryPersistenceMapper mapper;
    private final EntityManager entityManager;

    @Override
    public List<MealCategory> findAll() {
        try {
            return jpaRepository.findAll().stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot load categories", exception);
        }
    }

    @Override
    public Optional<MealCategory> findById(UUID id) {
        try {
            return jpaRepository.findById(id).map(mapper::toDomain);
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot load category", exception);
        }
    }

    @Override
    public Optional<MealCategory> findByPath(String path) {
        try {
            return jpaRepository.findByPath(path).map(mapper::toDomain);
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot load category by path", exception);
        }
    }

    @Override
    public List<MealCategory> findByPathPrefix(String pathPrefix) {
        try {
            return jpaRepository.findByPathStartingWith(pathPrefix).stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot load category descendants", exception);
        }
    }

    @Override
    public boolean existsByParentId(UUID parentId) {
        try {
            return jpaRepository.existsByParent_Id(parentId);
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot check category children", exception);
        }
    }

    @Override
    public MealCategory save(MealCategory category) {
        try {
            var entity = category.id() == null
                    ? new MealCategoryEntity()
                    : jpaRepository.findById(category.id()).orElseGet(MealCategoryEntity::new);
            mapper.copyToEntity(category, entity, parentReference(category.parentId()));
            return mapper.toDomain(jpaRepository.saveAndFlush(entity));
        } catch (DataAccessException exception) {
            if (isUniqueCategoryConstraint(exception)) {
                throw new DuplicateCategoryException(category.path());
            }
            throw new CategoryPersistenceException("Cannot save category", exception);
        }
    }

    @Override
    public List<MealCategory> saveAll(List<MealCategory> categories) {
        try {
            return categories.stream().map(this::save).toList();
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot save categories", exception);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            jpaRepository.deleteById(id);
        } catch (DataAccessException exception) {
            throw new CategoryPersistenceException("Cannot delete category", exception);
        }
    }

    private MealCategoryEntity parentReference(UUID parentId) {
        return parentId == null ? null : entityManager.getReference(MealCategoryEntity.class, parentId);
    }

    private boolean isUniqueCategoryConstraint(DataAccessException exception) {
        var message = String.valueOf(exception.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
        return message.contains("uk_meal_categories_path")
                || message.contains("uk_meal_categories_parent_slug");
    }
}

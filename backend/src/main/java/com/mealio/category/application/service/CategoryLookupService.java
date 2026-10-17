package com.mealio.category.application.service;

import com.mealio.category.api.CategoryLookup;
import com.mealio.category.api.CategoryLookupUnavailableException;
import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.port.out.CategoryRepository;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryLookupService implements CategoryLookup {
    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findExistingIds(Set<UUID> ids) {
        try {
            return repository.findExistingIds(ids);
        } catch (CategoryPersistenceException exception) {
            throw new CategoryLookupUnavailableException(exception);
        }
    }
}

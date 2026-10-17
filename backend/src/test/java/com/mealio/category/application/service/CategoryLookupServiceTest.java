package com.mealio.category.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mealio.category.api.CategoryLookupUnavailableException;
import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.port.out.CategoryRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryLookupServiceTest {
    private final CategoryRepository repository = mock(CategoryRepository.class);
    private final CategoryLookupService service = new CategoryLookupService(repository);

    @Test
    void returnsOnlyExistingIds() {
        var existing = UUID.randomUUID();
        var requested = Set.of(existing, UUID.randomUUID());
        when(repository.findExistingIds(requested)).thenReturn(Set.of(existing));
        assertThat(service.findExistingIds(requested)).containsExactly(existing);
    }

    @Test
    void exposesPublicFailureWithoutLeakingDomainException() {
        var ids = Set.of(UUID.randomUUID());
        var failure = new CategoryPersistenceException("Cannot load categories", new IllegalStateException());
        when(repository.findExistingIds(ids)).thenThrow(failure);
        assertThatThrownBy(() -> service.findExistingIds(ids))
                .isInstanceOf(CategoryLookupUnavailableException.class).hasCause(failure);
    }
}

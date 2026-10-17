package com.mealio.category.api;

import java.util.Set;
import java.util.UUID;

public interface CategoryLookup {
    Set<UUID> findExistingIds(Set<UUID> ids);
}

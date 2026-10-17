package com.mealio.category.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.*;

import com.mealio.category.domain.exception.InvalidCategoryException;
import com.mealio.category.domain.model.CategoryType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryPatchRequestTest {
    @Test
    void preservesMissingAndExplicitNullFieldsAndMapsValidValues() {
        var id = UUID.randomUUID();
        var patch = CategoryPatchRequest.from(Map.of("name", "Lunch", "parentId", id.toString(),
                "type", "MEAL", "sortOrder", 3));
        assertThat(patch.name().value()).isEqualTo("Lunch");
        assertThat(patch.parentId().value()).isEqualTo(id);
        assertThat(patch.type().value()).isEqualTo(CategoryType.MEAL);
        assertThat(patch.sortOrder().value()).isEqualTo(3);
        assertThat(patch.toCommand(id).id()).isEqualTo(id);
        var values = new HashMap<String, Object>();
        for (var key : new String[]{"name", "parentId", "type", "sortOrder"}) values.put(key, null);
        var nulls = CategoryPatchRequest.from(values);
        assertThat(nulls.name().present()).isTrue();
        assertThat(nulls.name().value()).isNull();
        assertThat(nulls.parentId().value()).isNull();
        assertThat(nulls.type().value()).isNull();
        assertThat(nulls.sortOrder().value()).isNull();
        var missing = CategoryPatchRequest.from(Map.of("sortOrder", 1));
        assertThat(missing.name().present()).isFalse();
        assertThat(missing.parentId().present()).isFalse();
        assertThat(missing.type().present()).isFalse();
        assertThat(CategoryPatchRequest.from(Map.of("name", "Lunch")).sortOrder().present()).isFalse();
    }

    @Test
    void acceptsOnlyExactIntegersInRange() {
        for (Number number : new Number[]{1, (short) 1, (byte) 1, 1L, BigInteger.ONE, BigDecimal.ONE}) {
            assertThat(CategoryPatchRequest.from(Map.of("sortOrder", number)).sortOrder().value()).isEqualTo(1);
        }
        for (Number number : new Number[]{1.0, 1.0f, Long.MAX_VALUE, new BigDecimal("1.1"),
                BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)}) {
            assertThatThrownBy(() -> CategoryPatchRequest.from(Map.of("sortOrder", number)))
                    .isInstanceOf(InvalidCategoryException.class).hasMessageContaining("within range");
        }
    }

    @Test
    void rejectsMalformedPayloadsAndTypes() {
        assertThatThrownBy(() -> CategoryPatchRequest.from(null)).isInstanceOf(InvalidCategoryException.class);
        assertThatThrownBy(() -> CategoryPatchRequest.from(Map.of())).isInstanceOf(InvalidCategoryException.class);
        for (Map<String, Object> fields : java.util.List.<Map<String, Object>>of(Map.of("other", 1),
                Map.of("name", 2), Map.of("parentId", 3), Map.of("parentId", "invalid"),
                Map.of("type", 4), Map.of("type", "invalid"), Map.of("sortOrder", "1"))) {
            assertThatThrownBy(() -> CategoryPatchRequest.from(fields)).isInstanceOf(InvalidCategoryException.class);
        }
    }
}

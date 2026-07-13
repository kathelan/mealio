package com.mealio.category.adapter.in.web;

import com.mealio.category.adapter.in.web.dto.CategoryCreateRequest;
import com.mealio.category.adapter.in.web.dto.CategoryPatchRequest;
import com.mealio.category.adapter.in.web.dto.CategoryResponse;
import com.mealio.category.domain.port.in.ManageCategoriesUseCase;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ManageCategoriesUseCase manageCategoriesUseCase;

    @GetMapping
    public List<CategoryResponse> list() {
        return manageCategoriesUseCase.list().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable UUID id) {
        return CategoryResponse.from(manageCategoriesUseCase.get(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryCreateRequest request) {
        var created = manageCategoriesUseCase.create(request.toCommand());
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + created.id()))
                .body(CategoryResponse.from(created));
    }

    @PatchMapping("/{id}")
    public CategoryResponse patch(@PathVariable UUID id, @RequestBody Map<String, Object> request) {
        return CategoryResponse.from(manageCategoriesUseCase.patch(CategoryPatchRequest.from(request).toCommand(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        manageCategoriesUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}

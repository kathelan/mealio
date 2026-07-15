package com.mealio.recipe.adapter.in.web;

import com.mealio.recipe.adapter.in.web.dto.IngredientCreateRequest;
import com.mealio.recipe.adapter.in.web.dto.IngredientResponse;
import com.mealio.recipe.adapter.in.web.dto.RecipeCreateRequest;
import com.mealio.recipe.adapter.in.web.dto.RecipeDetailsResponse;
import com.mealio.recipe.adapter.in.web.dto.RecipeResponse;
import com.mealio.recipe.domain.port.in.ManageRecipesUseCase;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final ManageRecipesUseCase recipes;

    @GetMapping
    public List<RecipeResponse> list() { return recipes.list().stream().map(RecipeResponse::from).toList(); }

    @GetMapping("/{id}")
    public RecipeDetailsResponse get(@PathVariable UUID id) { return RecipeDetailsResponse.from(recipes.get(id)); }

    @PostMapping
    public ResponseEntity<RecipeResponse> create(@RequestBody RecipeCreateRequest request) {
        var created = recipes.create(request.toCommand());
        return ResponseEntity.created(URI.create("/api/v1/recipes/" + created.id())).body(RecipeResponse.from(created));
    }

    @PostMapping("/{id}/ingredients")
    public ResponseEntity<IngredientResponse> addIngredient(@PathVariable UUID id,
                                                            @RequestBody IngredientCreateRequest request) {
        var created = recipes.addIngredient(request.toCommand(id));
        return ResponseEntity.created(URI.create("/api/v1/recipes/" + id + "/ingredients/" + created.id()))
                .body(IngredientResponse.from(created));
    }
}

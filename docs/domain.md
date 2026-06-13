# Domain Model

## Initial concepts

- MealCategory
- Recipe
- RecipeIngredient
- IngredientUnit

## MealCategory

Represents a generic grouping for recipes or meals.

Examples:

- breakfast
- lunch
- dinner
- sweet
- savory

## Recipe

Represents a stored cooking or meal item with metadata.

Expected fields:

- id
- name
- description
- createdAt
- photoUrl
- categoryId

## RecipeIngredient

Represents one ingredient line in a recipe.

Expected fields:

- id
- recipeId
- name
- quantity
- unit
- calories

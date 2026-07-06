# Domain Model

## Initial concepts

- MealCategory
- Recipe
- RecipeIngredient
- IngredientUnit

## MealCategory

Represents a generic grouping for recipes or meals. Categories are user-configurable
and can be nested under another category.

Examples:

- breakfast
- lunch
- dinner
- sweet
- savory

Expected fields:

- id
- parentId
- name
- slug
- path
- type
- sortOrder

Example tree:

- breakfast
- breakfast / sweet
- breakfast / savory
- dinner
- dinner / soup
- dinner / pasta

## Recipe

Represents a stored cooking or meal item with metadata.

Expected fields:

- id
- name
- description
- createdAt
- photoUrl
- categoryIds

## RecipeIngredient

Represents one ingredient line in a recipe.

Expected fields:

- id
- recipeId
- name
- quantity
- unit
- calories

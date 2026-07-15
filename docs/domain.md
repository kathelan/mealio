# Model domenowy

## Początkowe pojęcia

- MealCategory
- Recipe
- RecipeIngredient
- IngredientUnit

## MealCategory

Reprezentuje ogólne grupowanie przepisów lub posiłków. Kategorie są konfigurowane przez
użytkownika i mogą być zagnieżdżane w innych kategoriach.

Przykłady:

- śniadanie
- lunch
- obiad
- słodkie
- wytrawne

Oczekiwane pola:

- id
- parentId
- name
- slug
- path
- type
- sortOrder

Przykładowe drzewo:

- breakfast
- breakfast / sweet
- breakfast / savory
- dinner
- dinner / soup
- dinner / pasta

## Recipe

Reprezentuje zapisany przepis lub posiłek wraz z metadanymi.

Oczekiwane pola:

- id
- name
- description
- createdAt
- photoUrl
- categoryIds

## RecipeIngredient

Reprezentuje pojedynczą pozycję składnika w przepisie.

Oczekiwane pola:

- id
- recipeId
- name
- quantity
- unit
- calories

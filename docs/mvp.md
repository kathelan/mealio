# MVP

The first MVP should stay intentionally small.

## Scope

- create generic meal categories, such as breakfast, sweet, and savory
- create recipes with name, description, date, and photo reference
- add ingredients with quantity, unit, and calories
- support units like `g`, `t`, and `w`
- expose a health endpoint for OpenShift

## Out of scope

- meal diary
- weight tracking
- AI features
- social features
- shopping lists

## Why this shape

This keeps the first version focused on one valuable loop: define categories, store recipes,
and support the data needed for future diary and nutrition tracking features.

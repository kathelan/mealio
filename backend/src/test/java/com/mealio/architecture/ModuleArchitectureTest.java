package com.mealio.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ModuleArchitectureTest {

    private static final Set<String> MODULES = Set.of(
            "category", "recipe", "product", "diary", "nutrition", "mealparser");
    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.mealio");

    // Existing debt from ADR-0002. Remove these pairs in phase 2; do not allow entire packages.
    private static final Set<String> LEGACY_CATEGORY_ACCESS = Set.of(
            "com.mealio.recipe.adapter.in.web.RecipeExceptionHandler -> "
                    + "com.mealio.category.adapter.in.web.dto.ApiErrorResponse",
            "com.mealio.recipe.infrastructure.persistence.adapter.JpaRecipeRepository -> "
                    + "com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository",
            "com.mealio.adapter.out.persistence.entity.RecipeEntity -> "
                    + "com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity");

    private static final Set<String> LEGACY_HEALTH_ACCESS = Set.of(
            "com.mealio.application.service.HealthService -> com.mealio.adapter.in.web.dto.HealthResponse",
            "com.mealio.application.service.HealthService -> "
                    + "com.mealio.infrastructure.config.MealioApplicationProperties");

    @Test
    void domainsDoNotDependOnFrameworksOrAdapters() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta.persistence..", "org.hibernate..",
                        "..infrastructure..", "..adapter..", "..application..")
                .check(CLASSES);
    }

    @Test
    void applicationsDoNotDependOnAdaptersExceptForRecordedHealthDebt() {
        var adapterAccess = CLASSES.stream()
                .filter(origin -> origin.getPackageName().contains(".application."))
                .flatMap(origin -> origin.getDirectDependenciesFromSelf().stream())
                .filter(dependency -> dependency.getTargetClass().getPackageName().contains(".adapter.")
                        || dependency.getTargetClass().getPackageName().contains(".infrastructure."))
                .map(dependency -> dependency.getOriginClass().getName() + " -> "
                        + dependency.getTargetClass().getName())
                .collect(Collectors.toSet());
        assertThat(adapterAccess).containsExactlyInAnyOrderElementsOf(LEGACY_HEALTH_ACCESS);
    }

    @Test
    void modulesUseOnlyPublicApiExceptForRecordedCategoryDebt() {
        var internalAccess = CLASSES.stream()
                .flatMap(origin -> origin.getDirectDependenciesFromSelf().stream())
                .filter(dependency -> MODULES.contains(module(dependency.getTargetClass().getName())))
                .filter(dependency -> !module(dependency.getOriginClass().getName())
                        .equals(module(dependency.getTargetClass().getName())))
                .filter(dependency -> !isPublicApi(dependency.getTargetClass().getPackageName(),
                        module(dependency.getTargetClass().getName())))
                .map(dependency -> dependency.getOriginClass().getName() + " -> "
                        + dependency.getTargetClass().getName())
                .collect(Collectors.toSet());

        // Equality also forces removal of an exception when the corresponding dependency disappears.
        assertThat(internalAccess).containsExactlyInAnyOrderElementsOf(LEGACY_CATEGORY_ACCESS);
    }

    @Test
    void categoryDoesNotReturnToGlobalPersistenceOrModels() {
        noClasses().that().resideInAPackage("com.mealio.category..")
                .should().dependOnClassesThat().resideInAPackage("com.mealio.domain..")
                .check(CLASSES);

        var globalPersistenceAccess = CLASSES.stream()
                .filter(origin -> origin.getPackageName().startsWith("com.mealio.category."))
                .flatMap(origin -> origin.getDirectDependenciesFromSelf().stream())
                .filter(dependency -> dependency.getTargetClass().getPackageName()
                        .startsWith("com.mealio.adapter.out.persistence"))
                .map(dependency -> dependency.getTargetClass().getName())
                .collect(Collectors.toSet());
        assertThat(globalPersistenceAccess)
                .containsExactly("com.mealio.adapter.out.persistence.entity.BaseEntity");
    }

    @Test
    void businessModulesHaveNoCycles() {
        var modules = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(MODULES.stream().map(name -> "com.mealio." + name).toArray(String[]::new));
        slices().matching("com.mealio.(*)..")
                .should().beFreeOfCycles().check(modules);
    }

    private static String module(String className) {
        return className.startsWith("com.mealio.") ? className.substring("com.mealio.".length()).split("\\.")[0] : "";
    }

    private static boolean isPublicApi(String packageName, String module) {
        var apiPackage = "com.mealio." + module + ".api";
        return packageName.equals(apiPackage) || packageName.startsWith(apiPackage + ".");
    }
}

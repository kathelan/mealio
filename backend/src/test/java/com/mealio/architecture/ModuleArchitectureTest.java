package com.mealio.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleArchitectureTest {
    private static final Set<String> MODULES = Set.of(
            "category", "recipe", "product", "diary", "nutrition", "mealparser", "health");
    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.mealio");

    @Test
    void domainsDoNotDependOnFrameworksOrAdapters() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta.persistence..", "org.hibernate..",
                        "..infrastructure..", "..adapter..", "..application..")
                .check(CLASSES);
    }

    @Test
    void applicationsDoNotDependOnAdapters() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..infrastructure..")
                .check(CLASSES);
    }

    @Test
    void modulesUseOnlyPublicApi() {
        var internalAccess = CLASSES.stream()
                .flatMap(origin -> origin.getDirectDependenciesFromSelf().stream())
                .filter(dependency -> MODULES.contains(module(dependency.getTargetClass().getName())))
                .filter(dependency -> !module(dependency.getOriginClass().getName())
                        .equals(module(dependency.getTargetClass().getName())))
                .filter(dependency -> !isPublicApi(dependency.getTargetClass().getPackageName(),
                        module(dependency.getTargetClass().getName())))
                .map(dependency -> dependency.getDescription()).toList();
        assertThat(internalAccess).isEmpty();
    }

    @Test
    void globalLayersAndLegacyAdaptersDoNotReturn() {
        noClasses().should().resideInAnyPackage(
                "com.mealio.domain..", "com.mealio.application..", "com.mealio.adapter..", "com.mealio.*.adapter..")
                .check(CLASSES);
    }

    @Test
    void businessModulesHaveNoCycles() {
        var modules = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(MODULES.stream().map(name -> "com.mealio." + name).toArray(String[]::new));
        slices().matching("com.mealio.(*)..").should().beFreeOfCycles().check(modules);
    }

    private static String module(String className) {
        return className.startsWith("com.mealio.") ? className.substring("com.mealio.".length()).split("\\.")[0] : "";
    }

    private static boolean isPublicApi(String packageName, String module) {
        var apiPackage = "com.mealio." + module + ".api";
        return packageName.equals(apiPackage) || packageName.startsWith(apiPackage + ".");
    }
}

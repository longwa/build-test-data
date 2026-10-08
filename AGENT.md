# AGENT.md

This file provides guidance to a Coding AI when working with code in this repository.

## What This Project Is

`build-test-data` is a Grails plugin that simplifies test data creation by automatically satisfying domain class constraints. Instead of manually setting every required field, tests call `TestData.build(MyDomain)` and the plugin introspects constraints to generate valid values.

## Branches

Each major version has its own branch (`X.0.x`):

- `7.0.x` — Grails 8, Groovy 5, Java 21+
- `6.0.x` — Grails 7, Groovy 4, Java 17+

## Project Structure

This is a multi-project Gradle build:

- `plugin/` — the actual Grails plugin (`build-test-data`), published to Maven Central as `io.github.longwa:build-test-data`
- `examples/bookStore/` — a full Grails app with unit and integration tests; also contains `src/test/resources/TestDataConfig.groovy` showing configuration
- `examples/alternativeConfig/` — example demonstrating a custom config file name via `grails.buildtestdata.testDataConfig`
- `docs/` — the user guide, written in AsciiDoc (`docs/src/docs`) and built with Asciidoctor
- `buildSrc/` — Gradle build tooling

The plugin project is renamed from `:plugin` to `:build-test-data` in `settings.gradle`.

## Build & Test Commands

```bash
# Run all tests across all subprojects
./gradlew check

# Run only plugin tests
./gradlew :build-test-data:test

# Run only bookStore example tests
./gradlew :examples:bookStore:test
./gradlew :examples:bookStore:integrationTest

# Run a single test class (Spock spec)
./gradlew :build-test-data:test --tests "basetests.StringTests"

# Build without tests
./gradlew assemble

# Generate docs
./gradlew docs
```

Java 21 is required (see `.sdkmanrc`). Tests run with `useJUnitPlatform()` and locale forced to `en_US`.

## Core Architecture

### Builder Chain

`TestData` (the static entry point) maintains a cache of `DataBuilder` instances per class. Builders are resolved via a factory chain with Spring `@Order` priority:

1. `PersistentEntityDataBuilder` (highest priority) — GORM/Hibernate entities (`GormEntity` subclasses)
2. `ValidateableDataBuilder` — `@Validateable` command objects with `constraintsMap`
3. `PogoDataBuilder` — plain Groovy objects (fallback)

### How Constraint Satisfaction Works

`ValidateableDataBuilder` is the core engine:

1. Inspects `constraintsMap` to find all required (non-nullable) property names
2. For each unsatisfied property, iterates the applied constraints in priority order (defined in `CONSTRAINT_SORT_ORDER`)
3. Delegates to a `ConstraintHandler` (e.g., `EmailConstraintHandler`, `InListConstraintHandler`, `MatchesConstraintHandler`) to generate a valid value
4. Validates after each handler using the constraint itself; stops when satisfied

`PersistentEntityDataBuilder` extends this with GORM-specific behavior: bi-directional association wiring, ordering saves to avoid transient exceptions (saves `OneToOne` owning sides first), and `findOrBuild` support.

Grails 8 makes domain properties without an explicit `nullable` constraint nullable by default, so they are not populated unless the domain declares `nullable: false` or the app sets `grails.gorm.default.nullable: false`. The plugin and `bookStore` test apps set `grails.gorm.default.nullable: false`; `GormDefaultNullableSpec` covers the Grails 8 default.

### Test Traits for Plugin Consumers

The plugin exposes traits that consuming Grails apps use in tests:

- `BuildDataUnitTest` — unit test trait; extends `DependencyDataTest` (from Grails testing support) and auto-discovers `@Build` annotation to mock domains. Only for unit tests.
- `BuildDataTest` — deprecated alias of `BuildDataUnitTest`, will be removed in a future release
- `BuildDomainTest<D>` — single-domain unit test; generic version of `BuildDataUnitTest`
- `BuildHibernateTest` — for `HibernateSpec`-based tests; adds build methods via `MetaHelper`
- `TestDataBuilder` — lower-level trait for integration tests or any class; provides `build()` and `findOrBuild()` methods

### Configuration (`TestDataConfig.groovy`)

Placed in `src/test/resources/`. Loaded lazily from classpath. The config file name can be overridden via `grails.buildtestdata.testDataConfig` in `application.yml`.

```groovy
testDataConfig {
    sampleData {
        'com.example.MyDomain' {
            name = "fixed value"
            counter = {-> sequenceVar++ }  // closure for dynamic values
        }
    }
    unitAdditionalBuild = ['com.example.Hotel': [Author]]  // implicit @Build dependencies
    abstractDefault = ['com.example.AbstractBase': ConcreteImpl]  // polymorphic defaults
}
```

`TestDataConfigurationHolder` is the static holder; `TestDataConfigurationHolder.mergeConfig(closure)` allows per-test overrides (cleaned up via `@AfterAll`).

## Key Design Points

- `TestData.build(MyDomain, [name: 'override'])` — overrides specific properties while auto-satisfying the rest. `PogoDataBuilder` binds the map with `TestDataBinder`; a value that can't be converted to its property's type throws an `IllegalArgumentException`, and a key that isn't a property is logged as a warning and ignored
- `TestData.build([save: false], MyDomain)` — builds without persisting
- `TestData.build([find: true], MyDomain)` — finds an existing record or builds one
- `TestData.build([includes: ['optionalField']], MyDomain)` — also populate optional fields; `includes: '*'` populates all
- The `dk.brics:automaton` library is used by `MatchesConstraintHandler` and `UrlConstraintHandler` to generate strings matching regex patterns (`matches` constraint)
- The `Xeger.java` class (in `nl.flotsam.xeger`) generates strings from regular expressions using the automaton library

## Publishing & Versioning

Version is in `gradle.properties` (`projectVersion`). Snapshots publish to GitHub Packages; releases go to Maven Central via Sonatype. Releases are triggered by publishing a GitHub Release (e.g., `v7.0.0`) — see `RELEASE.md` for the full release process.

CI runs tests against Java 21 and 25 (`matrix.java` in `.github/workflows/gradle.yml`).
